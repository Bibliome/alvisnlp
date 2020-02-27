# Versioning policy

## Semantic Versioning

See [Semantic Versioning](https://semver.org/)

Versions of AlvisNLP take the form `MAJOR.MINOR.FIX`.

## Increment policy

| Type of change | Increment |
|-----|-----|
| Bug fix | FIX |
| Documentation update | FIX |
| Module behaviour change | FIX or MINOR |
| New parameter | FIX or MINOR |
| New module | MINOR |
| Parameter change | MINOR |
| New parameter | MINOR |
| Plan syntax change | MINOR |
| New core functionality | MINOR |
| CLI change | MINOR |
| Dependency update | MINOR |
| Build or install change | MINOR |

## MAJOR change policy

MAJOR changes must be anticipated as a set of changes. When all planned changes are effective, then the MAJOR changes.
FIXED or MINOR fixes planned for a MAJOR still increment.

Previous MAJOR versions will not be maintained, there will not be MINOR or FIX increments for old MAJOR versions.

There must be one GitHub project for each planned MAJOR increment.
There Each planned FIX and MINOR increment planned for this MAJOR m


## Tests policy

All tests must pass in order to increment FIX, MINOR or MAJOR.

## Module change policy

To increment MINOR for a new module:
* there must be a documentation for the module
* there must be a test that includes this module

TBC

## Online documentation

[Online documentation](https://bibliome.github.io/alvisnlp/) must be regenerated for each FIX, MINOR or MAJOR increment.

## Git tagging

Each FIX, MINOR or MAJOR increment must be tagged. The tag corresponding to a version must be an [annotated tag](https://git-scm.com/book/en/v2/Git-Basics-Tagging#_annotated_tags).

