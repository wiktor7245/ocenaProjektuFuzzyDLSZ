# -*- coding: utf-8 -*-

# https://towardsdatascience.com/extracting-text-from-scanned-pdf-using-pytesseract-open-cv-cd670ee38052

# use this command to install open cv2
# pip install opencv-python

# use this command to install PIL
# pip3 install Pillow

# use this command to install pdf2image
# pip3 install pdf2image

# use this command to install pytesseract
# pip3 install pytesseract

# use this command to install MagickWand
# pip3 install MagickWand

# use this command to install wand
# pip3 install wand

# install ghostscript
# https://www.ghostscript.com/download/gsdnld.html

# and tessertact
# https://github.com/UB-Mannheim/tesseract/wiki

# and magicwand
# pip3 install magicwand
import json
from PIL import Image
import io
import pytesseract
import sys
from wand.image import Image as wi


# print("hello from script!")

# import gc

# pytesseract.pytesseract.tesseract_cmd = 'C:\Program Files\Tesseract-OCR\\tesseract.exe'


def get_text_from_image(pdf_path):
    """ Extracting text content from Image  """

    json_data = {}

    pdf = wi(filename=pdf_path, resolution=300)
    pdfImg = pdf.convert('jpeg')
    imgBlobs = []
    extracted_text = []

    for img in pdfImg.sequence:
        page = wi(image=img)
        imgBlobs.append(page.make_blob('jpeg'))

    for imgBlob in imgBlobs:
        im = Image.open(io.BytesIO(imgBlob))
        text = pytesseract.image_to_string(im, lang='pol')
        text = text.replace(r"\n", " ")  # this might be bad
        extracted_text = text.split("\n")

    # print(extracted_text)

    for i in extracted_text:
        # print(i)
        if "Koszt: " in i:
            json_data['koszt'] = i[i.index("Koszt: ") + len("Koszt: "):]
        if "Czas trwania: " in i:
            json_data['czas_trwania'] = i[i.index(
                "Czas trwania: ") + len("Czas trwania: "):]
        if "Trudność projektu (w skali 1-10): " in i:
            json_data['trudnosc_projektu'] = i[i.index(
                "Trudność projektu (w skali 1-10): ") + len("Trudność projektu (w skali 1-10): "):]

    with open(pdf_path + '.json', 'w', encoding='utf-8') as outfile:
        json.dump(json_data, outfile, ensure_ascii=False)

    print("OK")


get_text_from_image(sys.argv[1])
