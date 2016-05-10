import os

# Contains functions for interacting with files


SOUNDS_DIR = 'res/sounds/'


def get_file_list_in_dir(directory, extension):
    file_list = []
    for fileName in os.listdir(os.path.abspath(directory)):
        if fileName.endswith(extension):
            file_list.append(os.path.abspath(SOUNDS_DIR + fileName))

    return file_list

