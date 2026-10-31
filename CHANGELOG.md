# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Leap spell now launches the caster with additional upward force.
- Spell recognition system status can now be checked with `/sephyr status` command.
- Microphone used for spell detection can now be selected with `/sephyr microphone <mic>`.
### Fixed
- `/sephyr cast` now works with the `/execute` command.
- Crash when it's not possible to start capturing the default microphone.
- Removed the leading `ʔ` from IPA of `ʔalɪˈvɑn`, which was duplicating `a`. This should improve the detection a little bit.
### Changed
- Spell recognition is now faster, and more accurate. The old 1.3GB detection model was replaced with a smaller 260MB one.
- Improved the look of the leap spell VFX.
- Made the leap spell apply smooth force over time instead of one big impulse.

## [0.1.0] - 2026-10-26

### Added
- Initial prototype release with 3 spells, to test the concept.

[unreleased]: https://github.com/SzczurekYT/Sephyr/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/SzczurekYT/Sephyr/releases/tag/v0.1.0
[SemVer]: https://semver.org
