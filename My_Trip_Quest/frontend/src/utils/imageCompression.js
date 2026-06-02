import imageCompression from 'browser-image-compression';

/**
 * Compresses an image file while preserving EXIF data.
 * Optimized for AI analysis (max 1024px, 0.8 quality).
 * 
 * @param {File} imageFile - The original image file
 * @returns {Promise<File>} - The compressed image file
 */
export const compressImage = async (imageFile) => {
    const options = {
        maxSizeMB: 0.5,        // Reduced to 0.5MB to stay well under API proxy limits
        maxWidthOrHeight: 800, // Reduced to 800px
        useWebWorker: true,
        initialQuality: 0.7,   // Slightly lower quality
        preserveExif: true,    // Still need this for location check fallback (if used)
        fileType: 'image/jpeg' // Force JPEG to save space
    };

    try {
        const compressedFile = await imageCompression(imageFile, options);
        console.log(`Image compressed: ${(imageFile.size / 1024 / 1024).toFixed(2)}MB -> ${(compressedFile.size / 1024 / 1024).toFixed(2)}MB`);

        // Safety check: if still > 2MB, try aggressive re-compression (metadata might be lost but better than failure)
        if (compressedFile.size > 2 * 1024 * 1024) {
            console.warn("Image still too large, re-compressing aggressively without EXIF...");
            const aggressiveOptions = { ...options, maxSizeMB: 0.2, maxWidthOrHeight: 600, preserveExif: false };
            return await imageCompression(imageFile, aggressiveOptions);
        }

        return compressedFile;
    } catch (error) {
        console.error('Image compression failed:', error);
        throw error;
    }
};
