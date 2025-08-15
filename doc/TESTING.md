# Testing Guide - GitHub Android App

## Overview
This project includes comprehensive testing setup to achieve 100% test coverage using Espresso for UI testing and JUnit for unit testing.

## Test Structure

### Unit Tests
- **Location**: `src/test/java/`
- **Framework**: JUnit 4, MockK, Turbine
- **Coverage**: ViewModels, Repository, Data Models

### UI Tests (Espresso)
- **Location**: `src/androidTest/java/`
- **Framework**: Espresso, Compose Testing
- **Coverage**: All screens and user interactions

### Test Files Created

#### Unit Tests
- `RepositoryViewModelTest.kt` - Repository search and trending functionality
- `UserViewModelTest.kt` - User profile and repositories
- `IssueViewModelTest.kt` - Issues management
- `AuthViewModelTest.kt` - Authentication flow
- `GithubRepositoryTest.kt` - Data layer testing
- `ExampleUnitTest.kt` - Basic unit test example
- `TestUtil.kt` - Base test utilities

#### UI Tests
- `HomeScreenTest.kt` - Repository list and search
- `RepositoryDetailScreenTest.kt` - Repository details
- `UserProfileScreenTest.kt` - User profile display
- `IssuesScreenTest.kt` - Issues management
- `AuthScreenTest.kt` - Authentication flow
- `CustomTestRunner.kt` - Hilt-compatible test runner

## Running Tests

### Unit Tests
```bash
./gradlew testDebugUnitTest
```

### UI Tests
```bash
./gradlew connectedDebugAndroidTest
```

### Coverage Report
```bash
./gradlew jacocoTestReport
```

The coverage report will be generated at:
- XML: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

## Test Configuration

### JaCoCo Configuration
- **Version**: 0.8.8
- **Coverage Targets**: 100% for both unit and instrumentation tests
- **Exclusions**: Generated code, Android framework classes, Hilt generated code

### Test Dependencies
- **MockK**: 1.13.9 - Mocking framework
- **Turbine**: 1.0.0 - Flow testing
- **Truth**: 1.4.0 - Assertion library
- **Robolectric**: 4.11.1 - Android framework testing
- **MockWebServer**: 4.12.0 - Network testing

### Espresso Dependencies
- **Espresso Core**: 3.5.1
- **Espresso Contrib**: 3.5.1
- **Espresso Intents**: 3.5.1
- **Espresso Web**: 3.5.1
- **Compose UI Test**: Latest stable
- **UI Automator**: 2.2.0

## Test Categories

### 1. ViewModel Tests
- State management
- Data flow testing
- Error handling
- Loading states

### 2. Repository Tests
- API integration
- Data mapping
- Error scenarios
- Pagination

### 3. UI Tests
- Screen navigation
- User interactions
- Error states
- Loading indicators
- Form validation

### 4. Integration Tests
- End-to-end flows
- Authentication
- Data persistence
- Network error handling

## Best Practices

### Unit Tests
- Use `runTest` for coroutine testing
- Mock external dependencies with MockK
- Test both success and error paths
- Verify state flow emissions with Turbine

### UI Tests
- Use test tags for Compose elements
- Test both portrait and landscape orientations
- Include accessibility testing
- Test with different screen sizes

### Coverage Guidelines
- Aim for 100% line coverage
- Include branch coverage where possible
- Test edge cases and error conditions
- Document untested code with justification

## Running Tests in CI/CD

### GitHub Actions Example
```yaml
name: Test and Coverage
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'adopt'
      - name: Run unit tests
        run: ./gradlew testDebugUnitTest
      - name: Run UI tests
        run: ./gradlew connectedDebugAndroidTest
      - name: Generate coverage report
        run: ./gradlew jacocoTestReport
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
```

## Troubleshooting

### Common Issues
1. **Hilt Testing**: Ensure test runner is properly configured
2. **Compose Testing**: Use proper test tags
3. **Coroutines**: Use `runTest` instead of `runBlocking`
4. **MockK**: Verify mock setup for suspend functions

### Performance Tips
- Use test orchestrator for parallel execution
- Enable test sharding for large test suites
- Use mock responses to reduce network dependencies
- Cache test dependencies in CI/CD

## Commands Summary

```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run UI tests only
./gradlew connectedDebugAndroidTest

# Generate coverage report
./gradlew jacocoTestReport

# Clean and rebuild
./gradlew clean build

# Run specific test class
./gradlew testDebugUnitTest --tests "*RepositoryViewModelTest*"

# Run tests with coverage
./gradlew createDebugCoverageReport
```