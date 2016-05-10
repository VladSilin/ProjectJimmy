import threading
import time

from playback.PlayThread import PlayThread

# This class allows for control over sound playback


class RandPlayer:
    def __init__(self, sounds, min_interval, max_interval):
        self.playable_sounds = sounds

        self.min_interval = min_interval
        self.max_interval = max_interval

        self.stop_event = threading.Event()
        self.is_playing = False
        self.stop_event.set()
        self.lock = threading.Lock()

    @property
    def min_interval(self):
        return self.__min_interval

    @min_interval.setter
    def min_interval(self, min_interval):
        self.__min_intereval = min_interval

    @property
    def max_interval(self):
        return self.__max_interval

    @max_interval.setter
    def max_interval(self, max_interval):
        self.__max_intereval = max_interval

    @property
    def playable_sounds(self):
        return self.__playable_sounds

    @playable_sounds.setter
    def playable_sounds(self, playable_sounds):
        self.__playable_sounds = playable_sounds

    def toggle(self):
        if self.stop_event.is_set(): 
            self.start_playback()
        else:
            self.stop_playback()

    def start_playback(self):
        self.stop_event.clear()
        play_thread = \
            PlayThread(self.min_interval, self.max_interval, self.playable_sounds, self.stop_event, self.lock)
        play_thread.start()
        self.is_playing = True

    def stop_playback(self):
        self.stop_event.set()
        self.is_playing = False

    def reset(self):
        if self.is_playing:
            self.stop_playback()
            time.sleep(1)
            self.start_playback()

