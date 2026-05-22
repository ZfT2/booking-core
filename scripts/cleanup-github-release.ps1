<#
.SYNOPSIS
Removes all GitHub release artifacts for a version.

.DESCRIPTION
Deletes GitHub releases, Git tags, and a GitHub Packages Maven package version.
The default mode is DryRun: it queries GitHub, but does not delete anything.

Required GitHub CLI auth scopes:
  repo, read:packages, delete:packages

.EXAMPLE
.\scripts\cleanup-github-release.ps1 -Version 0.5.2 -Tags "v0.5.1+patch,v0.5.2" -Mode DryRun

.EXAMPLE
.\scripts\cleanup-github-release.ps1 -Version 0.5.2 -Tags "v0.5.1+patch,v0.5.2" -Mode Execute
#>

[CmdletBinding()]
param(
	[Parameter(Mandatory = $true)]
	[string] $Version,

	[string[]] $Tags,

	[string] $Owner = "ZfT2",

	[string] $Repo = "booking-core",

	[string[]] $PackageNames = @("de.zft2.booking-core", "booking-core"),

	[ValidateSet("Auto", "Org", "User")]
	[string] $OwnerType = "Auto",

	[ValidateSet("Plan", "DryRun", "Execute")]
	[string] $Mode = "DryRun"
)

$ErrorActionPreference = "Stop"

function Split-List {
	param([string[]] $Values)

	$result = @()
	foreach ($value in $Values) {
		if ([string]::IsNullOrWhiteSpace($value)) {
			continue
		}
		foreach ($part in ($value -split ",")) {
			$trimmed = $part.Trim()
			if (-not [string]::IsNullOrWhiteSpace($trimmed)) {
				$result += $trimmed
			}
		}
	}
	return $result
}

function Escape-PathPart {
	param([string] $Value)
	return [System.Uri]::EscapeDataString($Value)
}

function Assert-GhCli {
	if ($Mode -eq "Plan") {
		return
	}

	if (-not (Get-Command gh -ErrorAction SilentlyContinue)) {
		throw "GitHub CLI 'gh' was not found in PATH."
	}

	& gh auth status *> $null
	if ($LASTEXITCODE -ne 0) {
		throw "GitHub CLI is not authenticated. Run: gh auth login --scopes 'repo,read:packages,delete:packages'"
	}
}

function Invoke-GhApi {
	param(
		[string] $Endpoint,
		[string] $Method = "GET",
		[switch] $AllowNotFound
	)

	if ($Mode -eq "Plan") {
		return $null
	}

	$arguments = @("api")
	if ($Method -ne "GET") {
		$arguments += @("-X", $Method)
	}
	$arguments += $Endpoint

	$output = & gh @arguments 2>&1
	$text = ($output | Out-String).Trim()

	if ($LASTEXITCODE -ne 0) {
		if ($AllowNotFound -and ($text -match "HTTP 404" -or $text -match "Not Found")) {
			return $null
		}
		throw "gh api $Method $Endpoint failed:`n$text"
	}

	if ([string]::IsNullOrWhiteSpace($text)) {
		return $null
	}

	return $text | ConvertFrom-Json
}

function Invoke-Delete {
	param(
		[string] $Endpoint,
		[string] $Label,
		[switch] $AllowNotFound
	)

	if ($Mode -eq "Plan") {
		Write-Host "[PLAN] DELETE $Endpoint ($Label)"
		return
	}

	if ($Mode -eq "DryRun") {
		Write-Host "[DRY-RUN] DELETE $Endpoint ($Label)"
		return
	}

	$result = Invoke-GhApi -Endpoint $Endpoint -Method "DELETE" -AllowNotFound:$AllowNotFound
	if ($AllowNotFound -and $null -eq $result) {
		Write-Host "[SKIP] Not found: $Label"
		return
	}

	Write-Host "[DELETE] $Label"
}

function Get-PackageScopes {
	if ($OwnerType -eq "Org") {
		return @("/orgs/$Owner")
	}
	if ($OwnerType -eq "User") {
		return @("/users/$Owner")
	}
	if ($Mode -eq "Plan") {
		return @("/orgs/$Owner", "/users/$Owner")
	}

	$repoInfo = Invoke-GhApi -Endpoint "/repos/$Owner/$Repo"
	if ($repoInfo.owner.type -eq "Organization") {
		return @("/orgs/$Owner")
	}
	return @("/users/$Owner")
}

function Remove-ReleaseForTag {
	param([string] $Tag)

	$encodedTag = Escape-PathPart -Value $Tag
	$lookupEndpoint = "/repos/$Owner/$Repo/releases/tags/$encodedTag"

	if ($Mode -eq "Plan") {
		Write-Host "[PLAN] Resolve release for tag $Tag via $lookupEndpoint"
		Invoke-Delete -Endpoint "/repos/$Owner/$Repo/releases/{release-id-for-$Tag}" -Label "release $Tag"
		return
	}

	$release = Invoke-GhApi -Endpoint $lookupEndpoint -AllowNotFound
	if ($null -eq $release) {
		Write-Host "[SKIP] Release not found for tag $Tag"
		return
	}

	Invoke-Delete -Endpoint "/repos/$Owner/$Repo/releases/$($release.id)" -Label "release $Tag"
}

function Remove-RemoteTag {
	param([string] $Tag)

	$encodedTag = Escape-PathPart -Value $Tag
	Invoke-Delete -Endpoint "/repos/$Owner/$Repo/git/refs/tags/$encodedTag" -Label "tag $Tag" -AllowNotFound
}

function Remove-PackageVersion {
	param([string] $PackageName)

	$encodedPackageName = Escape-PathPart -Value $PackageName
	$scopes = Get-PackageScopes

	foreach ($scope in $scopes) {
		$versionsEndpoint = "$scope/packages/maven/$encodedPackageName/versions?per_page=100"

		if ($Mode -eq "Plan") {
			Write-Host "[PLAN] Resolve Maven package version $PackageName / $Version via $versionsEndpoint"
			Invoke-Delete -Endpoint "$scope/packages/maven/$encodedPackageName/versions/{package-version-id-for-$Version}" -Label "package $PackageName version $Version"
			continue
		}

		$versions = Invoke-GhApi -Endpoint $versionsEndpoint -AllowNotFound
		if ($null -eq $versions) {
			Write-Host "[SKIP] Package not found at $scope/packages/maven/$PackageName"
			continue
		}

		$matches = @($versions | Where-Object { $_.name -eq $Version })
		if ($matches.Count -eq 0) {
			Write-Host "[SKIP] Version $Version not found in package $PackageName"
			continue
		}

		foreach ($packageVersion in $matches) {
			Invoke-Delete -Endpoint "$scope/packages/maven/$encodedPackageName/versions/$($packageVersion.id)" -Label "package $PackageName version $Version"
		}
	}
}

$Tags = Split-List -Values $Tags
if ($Tags.Count -eq 0) {
	$Tags = @("v$Version")
}
$PackageNames = Split-List -Values $PackageNames

Write-Host "GitHub cleanup for $Owner/$Repo"
Write-Host "Mode: $Mode"
Write-Host "Version: $Version"
Write-Host "Tags: $($Tags -join ', ')"
Write-Host "Package names: $($PackageNames -join ', ')"

Assert-GhCli

foreach ($tag in $Tags) {
	Remove-ReleaseForTag -Tag $tag
}

foreach ($packageName in $PackageNames) {
	Remove-PackageVersion -PackageName $packageName
}

foreach ($tag in $Tags) {
	Remove-RemoteTag -Tag $tag
}
