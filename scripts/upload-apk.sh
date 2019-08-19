#!/bin/sh
set -e

export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}
export DEVELOPMENT_BRANCH=${DEVELOPMENT_BRANCH:-development}

# Setup git
git config --global user.email "noreply@travis.com"
git config --global user.name "Travis CI" 

# Execute the following script only if it's merge to development or master branch
if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_REPO_SLUG" != "fossasia/neurolab-android" ] || ! [ "$TRAVIS_BRANCH" == "$DEVELOPMENT_BRANCH" -o "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    echo "We upload apk only for changes in Development or Master"
    exit 0
fi

# Generates app bundle
./gradlew bundleRelease

# Clone the repository
git clone --quiet --branch=apk https://fossasia:$GITHUB_KEY@github.com/fossasia/neurolab-android apk > /dev/null

cd apk

# Remove old files
if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    rm -rf neurolab-master*
else
    rm -rf neurolab-dev*
fi

# Copy apk and aab files
find ../app/build/outputs -type f -name '*.apk' -exec cp -v {} . \;
find ../app/build/outputs -type f -name '*.aab' -exec cp -v {} . \;

for file in app*; do
    if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
        if [[ ${file} =~ ".aab" ]]; then
            mv $file neurolab-master-${file}
        else
            mv $file neurolab-master-${file:4}
        fi
    elif [ "$TRAVIS_BRANCH" == "$DEVELOPMENT_BRANCH" ]; then
        if [[ ${file} =~ ".aab" ]]; then
                mv $file neurolab-dev-${file}
        else
                mv $file neurolab-dev-${file:4}
        fi
    fi
done

git checkout --orphan temporary

git add .
git commit -m "Travis build pushed to [$TRAVIS_BRANCH]"

# Delete current apk branch
git branch -D apk
# Rename current branch to apk
git branch -m apk

git push origin apk -f --quiet > /dev/null

# TODO: Prepare when a release is ready
# Publish App to Play Store
#if [ "$TRAVIS_BRANCH" == "$PUBLISH_BRANCH" ]; then
    #gem install fastlane
    #fastlane supply --aab neurolab-master-app.aab --skip_upload_apk true --track alpha --json_key ../scripts/fastlane.json --package_name $PACKAGE_NAME
#fi
