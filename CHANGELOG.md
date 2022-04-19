# Changelog

## [Unreleased]
### Added
- Test Case ID templating, by @HardNorth
### Changed
- Client version updated on [5.1.9](https://github.com/reportportal/client-java/releases/tag/5.1.9), by @HardNorth
- Slf4j version updated on 1.7.36, by @HardNorth

## [5.1.1]
### Fixed
- Invalid Data Tables format for some Report Portal versions
### Changed
- Client version updated on [5.1.4](https://github.com/reportportal/client-java/releases/tag/5.1.4)
- Slf4j version updated on 1.7.32 to support newer versions of Logback with security fixes

## [5.1.0]
### Changed
- Version promoted to stable release
- Client version updated on [5.1.0](https://github.com/reportportal/client-java/releases/tag/5.1.0)

## [5.1.0-RC-3]
### Added
- Feature / Scenario / Step / Hook start methods which are overridable
- JSR-305 annotations
- `buildFinishTestItemRequest` overridable method
### Changed
- Client version updated on [5.1.0-RC-12](https://github.com/reportportal/client-java/releases/tag/5.1.0-RC-12)
### Fixed
- Probable agent crash on cucumber-groovy
### Removed
- deprecated code

## [5.1.0-RC-2]
### Changed
- Client version updated on [5.1.0-RC-6](https://github.com/reportportal/client-java/releases/tag/5.1.0-RC-6)

## [5.1.0-RC-1]
### Changed
- Client version updated on [5.1.0-RC-4](https://github.com/reportportal/client-java/releases/tag/5.1.0-RC-4)
- Version changed on 5.1.0
### Fixed
- Scenario outlines with dynamic names support
- Possible immutable list modification error in AbstractReporter#getParameters method

## [5.0.3]
### Fixed
- Table parameter handling for different reporters

## [5.0.2]
### Changed
- Data attachments honor names now
- Client version updated on [5.0.21](https://github.com/reportportal/client-java/releases/tag/5.0.21)

### Fixed
- Empty interrupted suite in case of duplicate step

## [5.0.1]
### Changed
- Client version updated on [5.0.15](https://github.com/reportportal/client-java/releases/tag/5.0.15)
### Fixed
- 'CHILD_START_TIME_EARLIER_THAN_PARENT' Exception in some cases

## [5.0.0]
### Added
- Docstring parameter handling
### Changed
- Many static methods from Util class were moved to AbstractReporter class and made protected to ease extension
- Client version updated on `5.0.12`

## [5.0.0-RC-1]
### Added
- Callback reporting
### Changed
- CodeRef is now relative
- Test step parameters handling
- Mime type processing for data embedding was improved
### Fixed
- Manually-reported nested steps now correctly fail all parents
### Removed
- Scenario Outline iteration number in item names, to not break re-runs

## [5.0.0-BETA-1]
### Fixed
- Incorrect item type settings
- Attribute reporting
- Test case id annotation on a step definition method reading
### Added
- Nested steps support

## [0.0.2-ALPHA]
### Added
- Test Case ID support
### Fixed
- codeRef reporting was added for every item in an item tree

## [0.0.1]
## Added
- Initial implementation