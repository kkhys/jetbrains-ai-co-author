# jetbrains-ai-co-author

![Build](https://github.com/kkhys/jetbrains-ai-co-author/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

A sophisticated IntelliJ Platform plugin that seamlessly integrates AI collaboration into your version control workflow by automatically adding Claude as a co-author to commit messages.

<!-- Plugin description -->
JetBrains AI Co-Author enhances your development workflow by providing a streamlined way to acknowledge AI assistance in your commits. The plugin adds a convenient button to the VCS commit dialog that automatically appends the standard Git co-authored-by trailer format for Claude AI, ensuring proper attribution for AI-assisted development work.
<!-- Plugin description end -->

## Features

**Seamless VCS Integration**
- Adds a dedicated "Add AI Co-Author" button directly in the IntelliJ commit dialog
- Integrates naturally with existing VCS workflows without disrupting established practices
- Works with all VCS systems supported by IntelliJ Platform

**Smart Text Field Detection**
- Automatically locates commit message text fields using advanced component tree traversal
- Supports both EditorTextField and JTextArea components across different IntelliJ versions
- Handles various UI implementations with robust fallback mechanisms

**Intelligent Duplication Prevention**
- Prevents duplicate co-author entries in commit messages
- Maintains clean commit history by checking for existing Claude co-author attributions
- Preserves other co-author entries while adding only necessary attributions

**User Experience Excellence**
- Provides immediate feedback through IntelliJ's native notification system
- Handles edge cases gracefully with informative user messaging
- Maintains consistent behavior across different IDE configurations

## Technical Architecture

**Core Components**

- **CommitMessageAiAssistant**: CheckinHandlerFactory implementation that provides VCS workflow integration
- **GenerateCommitMessageAction**: AnAction implementation that delivers the user interface and business logic
- **Dynamic UI Discovery**: Runtime component tree analysis for maximum compatibility

**Design Principles**

- **Thread Safety**: All UI modifications execute on the Event Dispatch Thread
- **Component Abstraction**: Supports multiple text component types for broad compatibility
- **Extensible Architecture**: Clean separation of concerns enabling future enhancements

## Installation

**Via JetBrains Marketplace**
1. Open Settings/Preferences → Plugins → Marketplace
2. Search for "jetbrains-ai-co-author"
3. Click Install and restart IDE

**Manual Installation**
1. Download the latest release from [GitHub Releases](https://github.com/kkhys/jetbrains-ai-co-author/releases/latest)
2. Open Settings/Preferences → Plugins → Install Plugin from Disk
3. Select the downloaded plugin file and restart IDE

## Usage

1. Open any project with VCS enabled
2. Stage your changes as usual
3. Open the commit dialog
4. Click the "Add AI Co-Author" button in the message action group
5. The plugin will automatically append `Co-Authored-By: Claude <noreply@anthropic.com>` to your commit message

## Development

**Prerequisites**
- JDK 21
- IntelliJ IDEA 2024.3+
- Gradle 9.0+

**Building the Plugin**
```bash
# Clone the repository
git clone https://github.com/kkhys/jetbrains-ai-co-author.git
cd jetbrains-ai-co-author

# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Launch development IDE
./gradlew runIde
```

**Quality Assurance**
```bash
# Run static analysis
./gradlew qodana

# Generate coverage reports
./gradlew koverXmlReport

# Verify plugin compatibility
./gradlew verifyPlugin
```

## Contributing

Contributions are welcome through pull requests. Please ensure:

- All tests pass (`./gradlew test`)
- Code follows project conventions
- New features include appropriate test coverage
- Changes are documented in CHANGELOG.md

## Roadmap

**Current Status**
The plugin currently supports Claude AI co-author attribution, providing a solid foundation for AI collaboration acknowledgment in version control workflows.

**Planned Enhancements**

**Multi-AI Agent Support**
- Support for additional AI assistants (GitHub Copilot, ChatGPT, Gemini, etc.)
- Configurable AI agent selection through plugin settings
- Dynamic co-author format based on selected AI service
- Multiple AI co-authors in single commit support

**Enhanced User Experience**
- Customizable co-author templates and formats
- Keyboard shortcuts for quick AI co-author addition
- Integration with popular AI coding assistants
- Commit message template system with AI attribution

**Advanced Configuration**
- Per-project AI agent preferences
- Team-wide configuration management
- Integration with IDE AI assistant plugins
- Automatic AI detection and attribution

**Workflow Integrations**
- Pre-commit hooks integration
- Git template system support
- Branch-specific AI attribution rules
- Integration with code review tools

We welcome community input on feature priorities and implementation approaches. Please open issues or discussions to share your ideas and use cases.

## License

This project is licensed under the terms specified in the [LICENSE](LICENSE) file.

## Technical Specifications

**Supported Platforms**
- IntelliJ IDEA Community/Ultimate 2024.3+
- All JetBrains IDEs based on IntelliJ Platform
- Compatible with all VCS systems supported by IntelliJ

**Core Technologies**
- Kotlin 2.x
- IntelliJ Platform SDK 2024.3+
- JVM 21+

**Build and Dependencies**
- Gradle with Kotlin DSL
- IntelliJ Platform Gradle Plugin
- Automated CI/CD via GitHub Actions

For detailed version specifications, see:
- [gradle.properties](gradle.properties) - Core project versions
- [gradle/libs.versions.toml](gradle/libs.versions.toml) - Dependency catalog
- [build.gradle.kts](build.gradle.kts) - Build configuration

---

Built with the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
