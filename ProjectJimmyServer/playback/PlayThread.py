import threading
import random
import pygame
import os

# This class is a thread responsible for playing sounds


class PlayThread (threading.Thread):
    def __init__(self, min_interval, max_interval, sound_list, stop_event, lock):
        threading.Thread.__init__(self)

        self.min_interval = min_interval
        self.max_interval = max_interval
        self.sound_list = sound_list

        self.stop_event = stop_event
        self.lock = lock

    def run(self):
        self.play_sounds()

    def play_sounds(self):
        print self.sound_list
        while not self.stop_event.is_set():
            self.lock.acquire()
            interval = random.uniform(self.min_interval, self.max_interval);
            self.stop_event.wait(interval)

            if not self.stop_event.is_set() and self.sound_list:
                sound = random.choice(self.sound_list)
                pygame.mixer.init()
                if os.path.isfile(sound):
                    pygame.mixer.music.load(sound)
                    pygame.mixer.music.play()

                while pygame.mixer.music.get_busy():
                    pygame.time.wait(10)
            self.lock.release()
