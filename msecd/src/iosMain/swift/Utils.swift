
import Foundation

@objc public class UnicodeStringHelper: NSObject {
    @objc public func scalarCodePoint(string: String, index: Int) -> UInt32 {
        return string[index].unicodeScalarCodePoint()
    }
}

private extension Character {
    func unicodeScalarCodePoint() -> UInt32 {
        let characterString = String(self)
        let scalars = characterString.unicodeScalars

        return scalars[scalars.startIndex].value
    }
}

private extension StringProtocol {
    subscript(offset: Int) -> Character {
        self[index(startIndex, offsetBy: offset)]
    }
}
