# Image Dithering

## Overview
Image dithering is a technique used to create the illusion of color depth in images with a limited color palette. This method distributes the colors in a way that creates the perception of a larger number of colors.

## Features
- Supports different dithering algorithms.
- Can process images of various formats.
- Provides options for adjusting the color palette.

## Installation
To use this repository, clone it to your local machine:

```bash
git clone https://github.com/mohamed-taha-elmeligy/image-dithering.git
cd image-dithering
```

Install the required dependencies:

```bash
pip install -r requirements.txt
```

## Usage
To use the dithering algorithms, run:

```bash
python dither.py --input <input_image> --output <output_image> --algorithm <dithering_algorithm>
```

### Parameters
- `--input`: Path to the input image file.
- `--output`: Path to save the dithered image.
- `--algorithm`: Choose from available dithering algorithms (e.g., Floyd-Steinberg, Ordered).

## Examples
### Example 1: Floyd-Steinberg Dithering
```bash
python dither.py --input image.png --output dithered_image.png --algorithm floyd-steinberg
```

### Example 2: Ordered Dithering
```bash
python dither.py --input image.jpg --output ordered_dithered_image.png --algorithm ordered
```

## Contributing
1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes and push to your branch.
4. Submit a pull request.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.