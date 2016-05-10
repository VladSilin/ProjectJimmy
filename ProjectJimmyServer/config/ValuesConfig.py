import ConfigParser
import os

# This class facilitates configuration of server values stored in a config file


class ValuesConfig:
    def __init__(self):
        self.config = ConfigParser.ConfigParser()
        self.filename = "values_config.ini"
        self.config.readfp(open(os.path.abspath("config/" + self.filename)))

    def set_min_interval(self, value):
        self.config.set("Intervals", "min_interval", value)
        with open(os.path.abspath("config/" + self.filename), 'w') as configfile:
            self.config.write(configfile)

    def set_max_interval(self, value):
        self.config.set("Intervals", "max_interval", value)
        with open(os.path.abspath("config/" + self.filename), 'w') as configfile:
            self.config.write(configfile)

    def get_min_interval(self):
        return self.config.getint("Intervals", "min_interval")

    def get_max_interval(self):
        return self.config.getint("Intervals", "max_interval")
