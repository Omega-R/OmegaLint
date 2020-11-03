# OmegaLint
Add it in your root build.gradle at the end of repositories:

allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
Step 2. Add the dependency

dependencies {
        implementation 'com.github.omega-r:omegalint:de8bfabedee9434b8d4582161bba56f8b8349a8f'
}
