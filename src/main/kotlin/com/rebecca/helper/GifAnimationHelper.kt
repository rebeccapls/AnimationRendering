package com.rebecca.helper

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.metadata.IIOInvalidTreeException
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.FileImageOutputStream

/**
 * @author Pazaz
 */
fun configure(meta: IIOMetadata, delayTime: String?, imageIndex: Int) {
    val metaFormat = meta.nativeMetadataFormatName
    require("javax_imageio_gif_image_1.0" == metaFormat) { "Unfamiliar gif metadata format: $metaFormat" }
    val root: org.w3c.dom.Node? = meta.getAsTree(metaFormat)

    //find the GraphicControlExtension node
    var child: org.w3c.dom.Node? = root!!.firstChild
    while (child != null) {
        if ("GraphicControlExtension" == child.nodeName) {
            break
        }
        child = child.nextSibling
    }
    val gce = child as IIOMetadataNode
    gce.setAttribute("userDelay", "FALSE")
    gce.setAttribute("delayTime", delayTime)
    gce.setAttribute("disposalMethod", "restoreToBackgroundColor")

    //only the first node needs the ApplicationExtensions node
    if (imageIndex == 0) {
        val aes = IIOMetadataNode("ApplicationExtensions")
        val ae = IIOMetadataNode("ApplicationExtension")
        ae.setAttribute("applicationID", "NETSCAPE")
        ae.setAttribute("authenticationCode", "2.0")
        val uo = byteArrayOf( //last two bytes is an unsigned short (little endian) that
            //indicates the the number of times to loop.
            //0 means loop forever.
            0x1, 0x0, 0x0
        )
        ae.userObject = uo
        aes.appendChild(ae)
        root.appendChild(aes)
    }
    try {
        meta.setFromTree(metaFormat, root)
    } catch (e: IIOInvalidTreeException) {
        //shouldn't happen
        throw Error(e)
    }
}

@Throws(Exception::class)
fun animationToGif(images: Array<BufferedImage?>, delays: Array<String?>, name: String): ByteArrayOutputStream {
    val iw = ImageIO.getImageWritersByFormatName("gif").next()
    val stream = ByteArrayOutputStream()
    val ios = FileImageOutputStream(File("./images/$name.gif"))
    iw.output = ios
    iw.prepareWriteSequence(null)
    for (i in images.indices) {
        val src = images[i]
        val iwp = iw.defaultWriteParam
        val metadata = iw.getDefaultImageMetadata(ImageTypeSpecifier(src), iwp)
        configure(metadata, delays[i], i)
        val ii = IIOImage(src, null, metadata)
        iw.writeToSequence(ii, null)
    }
    iw.endWriteSequence()
    ios.close()
    return stream
}
