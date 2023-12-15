package com.app.messenger.security.service;

import com.app.messenger.exception.CompressionException;
import com.app.messenger.exception.DecompressionException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class CompressionUtil {
    public byte[] compressByteArray(byte[] arrayToCompress) throws CompressionException {
        byte[] compressedArray;

        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(arrayToCompress);
        deflater.finish();

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(arrayToCompress.length)) {
            byte[] buffer = new byte[4096];
            while (!deflater.finished()) {
                int size = deflater.deflate(buffer);
                byteArrayOutputStream.write(buffer, 0, size);
                Arrays.fill(buffer, (byte) 0);
            }

            compressedArray = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new CompressionException("Compression exception");
        }

        return compressedArray;
    }

    public byte[] decompressByteArray(byte[] arrayToDecompress) throws DecompressionException {
        byte[] decompressedArray;

        Inflater inflater = new Inflater();
        inflater.setInput(arrayToDecompress);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(arrayToDecompress.length)) {
            byte[] buffer = new byte[4096];
            while (!inflater.finished()) {
                int size = inflater.inflate(buffer);
                byteArrayOutputStream.write(buffer, 0, size);
                Arrays.fill(buffer, (byte) 0);
            }

            decompressedArray = byteArrayOutputStream.toByteArray();
        } catch (IOException | DataFormatException e) {
            throw new DecompressionException("Decompression exception");
        }

        return decompressedArray;
    }
}
