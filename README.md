# AI Co-Author

![Build](https://github.com/kkhys/jetbrains-ai-co-author/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/me.kkhys.aiCoauthor.svg)](https://plugins.jetbrains.com/plugin/me.kkhys.aiCoauthor)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/me.kkhys.aiCoauthor.svg)](https://plugins.jetbrains.com/plugin/me.kkhys.aiCoauthor)

<!-- Plugin description -->
A JetBrains IDE plugin that adds a button to the commit dialog for appending `Co-Authored-By: Claude <noreply@anthropic.com>` to your commit messages.
<!-- Plugin description end -->

## Installation

### JetBrains Marketplace

1. Settings/Preferences > Plugins > Marketplace
2. Search for "AI Co-Author"
3. Install and restart

### Manual

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
