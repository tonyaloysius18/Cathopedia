import Foundation
import Vision
import ImageIO

let dir = CommandLine.arguments[1]
var result: [String: [[Double]]] = [:]
for name in try FileManager.default.contentsOfDirectory(atPath: dir).sorted() {
    let url = URL(fileURLWithPath: dir).appendingPathComponent(name)
    guard let src = CGImageSourceCreateWithURL(url as CFURL, nil),
          let img = CGImageSourceCreateImageAtIndex(src, 0, nil) else { result[name] = []; continue }
    let req = VNDetectFaceRectanglesRequest()
    try? VNImageRequestHandler(cgImage: img, options: [:]).perform([req])
    // Vision boxes are normalised with the origin bottom-left; flip to top-left.
    result[name] = (req.results ?? []).map { f in
        let b = f.boundingBox
        return [Double(b.minX), Double(1 - b.maxY), Double(b.width), Double(b.height), Double(f.confidence)]
    }
}
let data = try JSONSerialization.data(withJSONObject: result, options: [.sortedKeys])
FileManager.default.createFile(atPath: CommandLine.arguments[2], contents: data)
