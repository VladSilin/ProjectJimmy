from watchdog.events import PatternMatchingEventHandler

from db import database_helper
from filesystem.file_manager import get_file_list_in_dir


class SoundFileChangeHandler(PatternMatchingEventHandler):
    patterns = ["*.ogg"]

    def __init__(self, random_player):
        PatternMatchingEventHandler.__init__(self)
        self.random_player = random_player

    def process(self, event):
        database_helper.populate_sound_table(get_file_list_in_dir("res/sounds/", ".ogg"))
        self.random_player.playable_sounds = database_helper.get_sound_list()
        self.random_player.reset()

    def on_modified(self, event):
        self.process(event)

    def on_created(self, event):
        self.process(event)

    def on_deleted(self, event):
        self.process(event)
