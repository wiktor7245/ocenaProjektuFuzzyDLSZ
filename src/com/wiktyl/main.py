# https://towardsdatascience.com/extracting-text-from-scanned-pdf-using-pytesseract-open-cv-cd670ee38052

# use this command to install open cv2
# pip install opencv-python

# use this command to install PIL
# pip install Pillow

# use this command to install pdf2image
# pip install pdf2image

# use this command to install pytesseract
# pip install pytesseract

# use this command to install MagickWand
# pip install MagickWand

# use this command to install wand
# pip install wand

# install ghostscript
# https://www.ghostscript.com/download/gsdnld.html

# and tessertact
# https://github.com/UB-Mannheim/tesseract/wiki

print("hello from script!")

import pytesseract
import io
from PIL import Image
from wand.image import Image as wi
import gc
import json

pytesseract.pytesseract.tesseract_cmd = 'C:\Program Files\Tesseract-OCR\\tesseract.exe'

def get_text_from_image(pdf_path):
    """ Extracting text content from Image  """

    json_data = {}

    pdf=wi(filename=pdf_path,resolution=300)
    pdfImg=pdf.convert('jpeg')
    imgBlobs=[]
    extracted_text = []
    # try:
    #     for img in pdfImg.sequence:
    #         page=wi(image=img)
    #         imgBlobs.append(page.make_blob('jpeg'))
    #         # for i in range(0,5):
    #         #     [gc.collect() for i in range(0,10)]
    #
    #     for imgBlob in imgBlobs:
    #         im=Image.open(io.BytesIO(imgBlob))
    #         text=pytesseract.image_to_string(im,lang='pol')
    #         text = text.replace(r"\n", " ")
    #         extracted_text.append(text)
    #         # for i in range(0,5):
    #         #     [gc.collect() for i in range(0,10)]
    #     print("ext")
    #     print(extracted_text)
    #     print("extfin")
    #     for i in extracted_text:
    #         print(i)
    #         if "Koszt:" in i:
    #             print("XD")
    #             print(i)
    #             json_data['koszt'] = i
    #         # json_data.append(i)
    #     # json_data.append(extracted_text)
    #     with open(pdf_path + '.json','w',encoding='utf-8') as outfile:
    #         json.dump(json_data,outfile,ensure_ascii=False)
    #     # return (''.join([i.replace("\n"," ")for i in extracted_text]))
    #     # [gc.collect() for i in range(0,10)]
    # finally:
    #     # [gc.collect() for i in range(0,10)]
    #     img.destroy()

    for img in pdfImg.sequence:
        page=wi(image=img)
        imgBlobs.append(page.make_blob('jpeg'))
        # for i in range(0,5):
        #     [gc.collect() for i in range(0,10)]

    for imgBlob in imgBlobs:
        im=Image.open(io.BytesIO(imgBlob))
        text=pytesseract.image_to_string(im,lang='pol')
        text = text.replace(r"\n", " ") #this might be bad
        extracted_text = text.split("\n")
        # for i in range(0,5):
        #     [gc.collect() for i in range(0,10)]
    print(extracted_text)
    for i in extracted_text:
        print(i)
        if "Koszt:" in i:
            print("XD")
            print(i)
            json_data['koszt'] = i[i.index("Koszt: ") + len("Koszt: "):]
            # s1[s1.index(s2) + len(s2):]
        # json_data.append(i)
    # json_data.append(extracted_text)
    with open(pdf_path + '.json','w',encoding='utf-8') as outfile:
        json.dump(json_data,outfile,ensure_ascii=False)
    # return (''.join([i.replace("\n"," ")for i in extracted_text]))
    # [gc.collect() for i in range(0,10)]
# print(get_text_from_image("file.pdf"))
# get_text_from_image("file.pdf")
get_text_from_image("test_project.pdf")