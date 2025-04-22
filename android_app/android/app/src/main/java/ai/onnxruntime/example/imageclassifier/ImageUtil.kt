//package ai.onnxruntime.example.imageclassifier
/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.onnxruntime.example.imageclassifier

import android.graphics.*
import androidx.camera.core.ImageProxy
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.nio.FloatBuffer

const val DIM_BATCH_SIZE = 1;
const val DIM_PIXEL_SIZE = 3;
const val IMAGE_SIZE_X = 224;
const val IMAGE_SIZE_Y = 224;

fun preProcess(bitmap: Bitmap): FloatBuffer {
    val imgData = FloatBuffer.allocate(
            DIM_BATCH_SIZE
                    * DIM_PIXEL_SIZE
                    * IMAGE_SIZE_X
                    * IMAGE_SIZE_Y
    )
    imgData.rewind()
    val stride = IMAGE_SIZE_X * IMAGE_SIZE_Y
    val bmpData = IntArray(stride)
    bitmap.getPixels(bmpData, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    for (i in 0..IMAGE_SIZE_X - 1) {
        for (j in 0..IMAGE_SIZE_Y - 1) {
            val idx = IMAGE_SIZE_Y * i + j
            val pixelValue = bmpData[idx]
            imgData.put(idx, (((pixelValue shr 16 and 0xFF) / 255f - 0.485f) / 0.229f))
            imgData.put(idx + stride, (((pixelValue shr 8 and 0xFF) / 255f - 0.456f) / 0.224f))
            imgData.put(idx + stride * 2, (((pixelValue and 0xFF) / 255f - 0.406f) / 0.225f))
        }
    }

    imgData.rewind()
    return imgData
}

//import android.graphics.Bitmap
//import org.opencv.android.Utils
//import org.opencv.core.*
//import org.opencv.imgcodecs.Imgcodecs
//import org.opencv.imgproc.Imgproc
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.FloatBuffer
//
//fun preProcessImage(bitmap: Bitmap): FloatBuffer {
//
//    // 1. 读取图像
//
//    val image = Mat()
//    Utils.bitmapToMat(bitmap, image)
//    // 2. 调整大小到模型输入尺寸
//    val resizedImage = Mat()
//    Imgproc.resize(image, resizedImage, Size(224.0, 224.0))
//
//    // 3. 转换通道顺序 BGR -> RGB
//    val rgbImage = Mat()
//    Imgproc.cvtColor(resizedImage, rgbImage, Imgproc.COLOR_BGR2RGB)
//
//    // 4. 归一化到 [0, 1]
//    rgbImage.convertTo(rgbImage, CvType.CV_32F,1.0 / 255.0)
//
//    // 5. 减去均值，除以标准差
//    val mean = Scalar(0.485, 0.456, 0.406)
//    val std = Scalar(0.229, 0.224, 0.225)
//    Core.subtract(rgbImage, mean, rgbImage)
//    Core.divide(rgbImage, std, rgbImage)
//
//    // 6. 转换为 ByteBuffer
//    val floatBuffer = FloatBuffer.allocate(4 * 224 * 224 * 3)
////    val pixels = FloatArray(224 * 224 * 3)
//    for (y in 0 until rgbImage.rows()) {
//        for (x in 0 until rgbImage.cols()) {
//            val pixel = rgbImage.get(y, x)
//            floatBuffer.put(pixel[0].toFloat()) // R
//            floatBuffer.put(pixel[1].toFloat()) // G
//            floatBuffer.put(pixel[2].toFloat()) // B
//        }
//    }
////    rgbImage.get(0, 0, pixels) // 获取像素值
////    floatBuffer.put(pixels) // 写入 FloatBuffer
//    floatBuffer.rewind() // 重置指针，确保后续读取从起点开始
//
//    return floatBuffer
//}