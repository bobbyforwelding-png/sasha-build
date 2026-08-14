<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/8d6d1051-a79f-4d81-b309-d7e78502b1c9

## Run Locally

**Prerequisites:** [Android Studio](https://developer.android.com/studio)

1. Open Android Studio
2. Select **Open** and choose `/home/runner/work/sasha-build/sasha-build`
3. Allow Android Studio to complete Gradle sync
4. Create `/home/runner/work/sasha-build/sasha-build/local.properties` with:
   - `GEMINI_API_KEY=your_api_key_here`
   - (optional) `sdk.dir=/path/to/Android/Sdk`
5. Run the app on an emulator or physical device
