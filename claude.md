# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a JetBrains IntelliJ Platform plugin that adds AI co-author functionality to VCS commit workflows. The plugin integrates with IntelliJ's commit dialog to automatically add `Co-Authored-By: Claude <noreply@anthropic.com>` trailers to commit messages.

## Essential Commands

### Development Workflow
```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run all checks (tests + verification)
./gradlew check

# Verify plugin compatibility and structure
./gradlew verifyPlugin

# Launch development IDE with plugin loaded
./gradlew runIde
```

### Testing
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "me.kkhys.jetbrains.aiCoAuthor.AiCoAuthorPluginTest.testProjectInitialization"

# Generate coverage reports
./gradlew koverXmlReport
```

### Quality Assurance
```bash
# Static analysis
./gradlew qodana

# Plugin verification (compatibility with IntelliJ versions)
./gradlew verifyPlugin
```

## Architecture Overview

### Core Integration Pattern
The plugin uses IntelliJ Platform's extension point system with two main integration approaches:

1. **CheckinHandlerFactory** (`CommitMessageAiAssistant`): Provides the foundation for VCS workflow integration
2. **AnAction** (`GenerateCommitMessageAction`): Implements the actual UI button and logic

### Component Interaction Flow
```
plugin.xml defines extensions → CheckinHandlerFactory registers with VCS → 
AnAction provides UI button → Dynamic component tree search → 
Text field manipulation → User notification
```

### Key Technical Decisions

**Dynamic UI Component Discovery**: The plugin uses runtime component tree traversal to locate commit message text fields, supporting both `EditorTextField` and `JTextArea` components. This approach handles IntelliJ's varying UI implementations across versions.

**Thread Safety**: All UI modifications use `ApplicationManager.getApplication().invokeLater()` and `SwingUtilities.invokeLater()` to ensure proper EDT (Event Dispatch Thread) execution.

**Extension Point Strategy**: Rather than creating a single monolithic extension, the plugin separates concerns:
- `CheckinHandlerFactory` for VCS integration lifecycle
- `AnAction` for user interaction and business logic
- `NotificationGroup` for user feedback

## Development Context

### Plugin Configuration
- Target: IntelliJ Community 2024.3.6+
- Kotlin: 2.2.0 with JVM 21
- Testing: JUnit 4 + IntelliJ Platform Testing Framework

### Project Structure Patterns
```
src/main/kotlin/me/kkhys/jetbrains/aiCoAuthor/
├── vcs/           # VCS integration components
└── actions/       # UI actions and user interactions
```

### Testing Strategy
Tests use IntelliJ Platform's `BasePlatformTestCase` with test data in `src/test/testData/`. The testing approach focuses on:
- Basic plugin initialization verification
- XML file processing (template-based tests)
- Refactoring operation compatibility

### CI/CD Pipeline Integration
The project includes comprehensive GitHub Actions workflows:
- Build verification with artifact generation
- Test execution with coverage reporting (Codecov)
- Qodana static analysis
- IntelliJ Plugin Verifier compatibility checks
- Automated draft release creation

## Plugin Development Specifics

### Extension Registration
All extensions and actions are defined in `src/main/resources/META-INF/plugin.xml`. The plugin registers:
- `checkinHandlerFactory` for VCS integration
- `notificationGroup` for user messaging
- Action in `Vcs.MessageActionGroup` for UI accessibility

### IntelliJ Platform Dependencies
The plugin depends on `com.intellij.modules.platform` and uses IntelliJ Platform Gradle Plugin for dependency management and plugin packaging.