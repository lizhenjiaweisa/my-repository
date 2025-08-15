# GitHub Android App

A modern Android application for browsing GitHub repositories, built with Jetpack Compose, Hilt, and modern Android development practices.

## Features

- 🔍 **Repository Search**: Search GitHub repositories with language filtering
- 📈 **Trending Repositories**: View trending repositories by language
- 👤 **User Profiles**: View detailed user information and their repositories
- 🐛 **Issues Management**: Browse and create issues for repositories
- 🔐 **GitHub OAuth**: Secure authentication with GitHub OAuth
- 🎨 **Modern UI**: Beautiful Material Design 3 interface
- 📱 **Responsive Design**: Optimized for phones and tablets

## Tech Stack

### Architecture
- **MVVM Pattern**: Clean architecture with ViewModels
- **Jetpack Compose**: Modern declarative UI toolkit
- **Hilt**: Dependency injection framework
- **Navigation Component**: Type-safe navigation between screens

### Networking
- **Retrofit**: Type-safe HTTP client
- **OkHttp**: HTTP client with interceptors
- **Gson**: JSON serialization/deserialization

### Data Persistence
- **Room**: Local database for caching
- **DataStore**: Secure storage for authentication tokens
- **Coroutines**: Asynchronous programming

### Image Loading
- **Coil**: Efficient image loading and caching

## Setup Instructions

### Prerequisites

1. **Android Studio**: Latest stable version (Hedgehog or later)
2. **Android SDK**: API level 34 (Android 14)
3. **JDK**: Java 11 or higher

### GitHub OAuth Setup

1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App:
   - Application name: `GitHub Android App`
   - Homepage URL: `https://github.com`
   - Authorization callback URL: `githubapp://oauth/callback`
3. Note down your **Client ID** and **Client Secret**

### Project Configuration

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd github-android-app
   ```

2. **Configure local.properties**:
   ```properties
   # Copy local.properties.template to local.properties
   sdk.dir=C:\\Users\\[USERNAME]\\AppData\\Local\\Android\\Sdk
   github_client_id=your_actual_client_id
   github_client_secret=your_actual_client_secret
   ```

3. **Build the project**:
   ```bash
   ./gradlew build
   ```

4. **Run the app**:
   - Open in Android Studio
   - Click "Run" button or use `./gradlew installDebug`

## Project Structure

```
app/
├── src/main/java/com/github/app/
│   ├── data/
│   │   ├── model/          # Data models (Repository, User, Issue)
│   │   ├── remote/         # API service definitions
│   │   └── repository/     # Data repositories
│   ├── di/                 # Dependency injection modules
│   ├── presentation/
│   │   ├── component/      # Reusable UI components
│   │   ├── navigation/     # Navigation graph
│   │   ├── screen/         # UI screens
│   │   ├── theme/          # Theme and styling
│   │   └── viewmodel/      # ViewModels
│   └── GitHubApplication.kt # Application class
```

## Key Components

### Screens
- **HomeScreen**: Repository search and trending
- **RepositoryDetailScreen**: Detailed repository information
- **UserProfileScreen**: User profile and repositories
- **IssuesScreen**: Repository issues management
- **AuthScreen**: GitHub OAuth authentication

### ViewModels
- **RepositoryViewModel**: Repository data and search
- **UserViewModel**: User profile and repositories
- **IssueViewModel**: Issues management
- **AuthViewModel**: Authentication state

### Components
- **RepositoryCard**: Repository list item
- **SearchBar**: Search functionality
- **LanguageFilter**: Language selection dropdown

## Build Variants

- **debug**: Development build with debugging enabled
- **release**: Production build with code shrinking and optimization

## Testing

The app includes comprehensive testing:
- **Unit Tests**: ViewModel and repository tests
- **UI Tests**: Compose UI testing
- **Integration Tests**: API and database testing

Run tests:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

## Development

### Code Style
- Follows [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Uses [ktlint](https://ktlint.github.io/) for code formatting

### Architecture Guidelines
- Single source of truth for data
- Unidirectional data flow
- Separation of concerns
- Reactive programming with Flow

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please open an issue on the GitHub repository or contact the development team.

---

**Built with ❤️ using modern Android development practices**