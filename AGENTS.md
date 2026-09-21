# Sephyr

Voice-powered spellcasting mod for Minecraft. Multi-loader architecture supporting both Fabric and NeoForge.

## Repository Structure

- **common/**: Shared code (loader-agnostic)
- **fabric/**: Fabric-specific implementation
- **neoforge/**: NeoForge-specific implementation
- **sepple/java/**: Java bridge to Rust voice recognition (J4RS)
- **sepple/rust/**: Rust voice recognition library
- **build-logic/**: Custom Gradle convention plugins

## Build Commands

```bash
# Full build
./gradlew build

# Rust natives (one-time Docker setup)
./gradlew sepple:buildDockerImages
./gradlew sepple:buildRustNatives

# Rust natives (dev/debug mode, faster)
./gradlew sepple:buildRustNativesDev
```

## Key Facts

- Java version: 25
- Minecraft version: 26.1.2
- Rust cross-compilation requires Docker
- Cross-compiled targets: x86_64/aarch64 for Linux, macOS, Windows
- Rust natives are LZ4-compressed and bundled into resources
- No CI, no tests, no linting tools configured

## Architecture

- Uses `ServiceLoader` pattern for platform abstraction (`Services.java`)
- Rust voice recognition pipeline: `AudioCapture -> AudioChunker -> VAD -> WordDetector`
- J4RS enables Java-Rust interop via `SeppleBinding` class

## Conventions

- Package root: `yt.szczurek.sephyr` (common), platform-specific subpackages under each loader
- Rust code lives in `sepple/rust/` and must be built separately from Java
