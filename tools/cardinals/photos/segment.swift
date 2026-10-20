import Foundation
import Vision
import ImageIO
import CoreImage
import UniformTypeIdentifiers

let dir = CommandLine.arguments[1], out = CommandLine.arguments[2]
let ctx = CIContext()
for name in try FileManager.default.contentsOfDirectory(atPath: dir).sorted() {
    let url = URL(fileURLWithPath: dir).appendingPathComponent(name)
    guard let src = CGImageSourceCreateWithURL(url as CFURL, nil),
          let img = CGImageSourceCreateImageAtIndex(src, 0, nil) else { print("skip \(name)"); continue }
    let req = VNGeneratePersonSegmentationRequest()
    req.qualityLevel = .accurate
    req.outputPixelFormat = kCVPixelFormatType_OneComponent8
    do { try VNImageRequestHandler(cgImage: img, options: [:]).perform([req]) } catch { print("fail \(name)"); continue }
    guard let buf = req.results?.first?.pixelBuffer else { print("none \(name)"); continue }
    var mask = CIImage(cvPixelBuffer: buf)
    // Scale the mask up to the photo's own size.
    mask = mask.transformed(by: CGAffineTransform(scaleX: CGFloat(img.width) / mask.extent.width,
                                                  y: CGFloat(img.height) / mask.extent.height))
    guard let cg = ctx.createCGImage(mask, from: CGRect(x: 0, y: 0, width: img.width, height: img.height)) else { continue }
    let dest = CGImageDestinationCreateWithURL(URL(fileURLWithPath: out).appendingPathComponent(name + ".png") as CFURL,
                                               UTType.png.identifier as CFString, 1, nil)!
    CGImageDestinationAddImage(dest, cg, nil)
    CGImageDestinationFinalize(dest)
}
