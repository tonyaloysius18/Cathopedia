import Foundation
import Vision
import ImageIO
import CoreImage
import UniformTypeIdentifiers

let dir = CommandLine.arguments[1], out = CommandLine.arguments[2]
let ctx = CIContext()
func save(_ buf: CVPixelBuffer, _ path: String, _ w: Int, _ h: Int) {
    var m = CIImage(cvPixelBuffer: buf)
    m = m.transformed(by: CGAffineTransform(scaleX: CGFloat(w) / m.extent.width, y: CGFloat(h) / m.extent.height))
    guard let cg = ctx.createCGImage(m, from: CGRect(x: 0, y: 0, width: w, height: h)) else { return }
    let d = CGImageDestinationCreateWithURL(URL(fileURLWithPath: path) as CFURL, UTType.png.identifier as CFString, 1, nil)!
    CGImageDestinationAddImage(d, cg, nil); CGImageDestinationFinalize(d)
}
for name in try FileManager.default.contentsOfDirectory(atPath: dir).sorted() {
    let url = URL(fileURLWithPath: dir).appendingPathComponent(name)
    guard let src = CGImageSourceCreateWithURL(url as CFURL, nil),
          let img = CGImageSourceCreateImageAtIndex(src, 0, nil) else { continue }
    let req = VNGeneratePersonInstanceMaskRequest()
    let handler = VNImageRequestHandler(cgImage: img, options: [:])
    do { try handler.perform([req]) } catch { print("fail \(name) \(error)"); continue }
    guard let obs = req.results?.first else { print("none \(name)"); continue }
    for i in obs.allInstances {
        if let buf = try? obs.generateScaledMaskForImage(forInstances: IndexSet(integer: i), from: handler) {
            save(buf, "\(out)/\(name)__\(i).png", img.width, img.height)
        }
    }
}
