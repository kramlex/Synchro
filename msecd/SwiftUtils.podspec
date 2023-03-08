Pod::Spec.new do |spec|
    spec.name                     = 'SwiftUtils'
    spec.version                  = '1.0.0'
    spec.homepage                 = 'Link'
    spec.source                   = { :git => "Not Published", :tag => "Cocoapods/#{spec.name}/#{spec.version}" }
    spec.authors                  = 'Mark Dubkov'
    spec.license                  = ''
    spec.summary                  = 'Objc SwiftUtils'
    spec.module_name              = "#{spec.name}"

    spec.source_files             = "src/iosMain/swift/**/*.{h,m,swift}"
    spec.resources                = "src/iosMain/bundle/**/*"

    spec.ios.deployment_target  = '12.0'
    spec.swift_version          = '5.0'

    spec.framework    = 'Foundation'

    spec.pod_target_xcconfig = {
        'VALID_ARCHS' => '$(ARCHS_STANDARD_64_BIT)'
    }
end
