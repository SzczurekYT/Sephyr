{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    rust-overlay.url = "github:oxalica/rust-overlay";
  };
  outputs =
    {
      nixpkgs,
      flake-utils,
      rust-overlay,
      ...
    }:
    flake-utils.lib.eachDefaultSystem (
      system:
      let
        overlays = [ (import rust-overlay) ];
        pkgs = import nixpkgs {
          inherit system overlays;
        };
        java = pkgs.temurin-bin-21;
        buildInputs = with pkgs; [
          # Minecraft deps
          libGL
          glfw3-minecraft
          flite # So there is no long lib not found error
          libx11 # Otherwise GLFW init fails with no platform found error
          libpulseaudio
          # Sepple deps
          alsa-lib
          openssl
        ];
      in
      with pkgs;
      {
        devShells.default = mkShell {
          inherit buildInputs;
          packages = [
            java
            rust-bin.stable.latest.default
            jetbrains.idea-oss
            # Needed by Sepple
            pkg-config
          ];
          env = {
            LD_LIBRARY_PATH = lib.makeLibraryPath buildInputs;
            JAVA_HOME = "${java.home}";
          };
        };
      }
    );
}
