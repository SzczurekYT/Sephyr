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
            libpulseaudio
            java
        ];
      in
      with pkgs;
      {
        devShells.default = mkShell {
          inherit buildInputs;
          packages = [
            rust-bin.stable.latest.default
            jetbrains.idea-oss
            # gradle_8
            # python314
            # uv
            # pkg-config
            # portaudio
            # alsa-lib
          ];
          env = {
            LD_LIBRARY_PATH = lib.makeLibraryPath buildInputs;
            JAVA_HOME = "${java.home}";
          };
        };
      }
    );
}
