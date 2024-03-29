Pod::Spec.new do |spec|
    spec.name                     = 'code_viewer'
    spec.version                  = '1.0-SNAPSHOT'
    spec.homepage                 = 'https://github.com/JetBrains/compose-jb'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Shared code for the sample'
    spec.vendored_frameworks      = 'build/cocoapods/framework/shared.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '14.1'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':code-viewer',
        'PRODUCT_MODULE_NAME' => 'shared',
    }
                
    spec.script_phases = [
        {
            :name => 'Build code_viewer',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../../../../private/var/folders/3b/1dwzdk496x5fy9fk5p3n71y80000gp/T/wrap9loc/gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.resources = ['src/commonMain/resources/**', 'src/iosMain/resources/**']
end