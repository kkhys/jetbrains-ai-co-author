# AI Co-Author

> [!IMPORTANT]
> This project has been discontinued and is no longer maintained. The plugin has been removed from JetBrains Marketplace and will not receive further updates. Existing installations continue to work, and manual installation from [Releases](https://github.com/kkhys/jetbrains-ai-co-author/releases/latest) is still possible.

<!-- Plugin description -->
A JetBrains IDE plugin that adds a button to the commit dialog for appending `Co-Authored-By: Claude <noreply@anthropic.com>` to your commit messages.
<!-- Plugin description end -->

## Installation

1. Download from [Releases](https://github.com/kkhys/jetbrains-ai-co-author/releases/latest)
2. Settings/Preferences > Plugins > Install Plugin from Disk...

## Usage

1. Open the commit dialog
2. Click "Add AI Co-Author" in the message action group
3. `Co-Authored-By: Claude <noreply@anthropic.com>` is appended to your message

## Development

Requires JDK 21.

```bash
./gradlew runIde       # Launch development IDE
./gradlew test         # Run tests
./gradlew buildPlugin  # Build distributable
```

## License

[MIT](LICENSE.md)
