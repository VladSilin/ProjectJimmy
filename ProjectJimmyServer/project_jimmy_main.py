from flask import Flask, jsonify, request
from watchdog.observers import Observer
import os

from SoundFileChangeHandler import SoundFileChangeHandler
from config import ValuesConfig
from db import database_helper
from filesystem.file_manager import get_file_list_in_dir
from playback.RandPlayer import RandPlayer

# This is the main Flask application module


EXTENSION = ".ogg"
SOUNDS_PATH = 'res/sounds/'

app = Flask(__name__)


@app.route('/projectjimmy/api/v1.0/config/isplaying', methods=['GET'])
def get_is_playing():
    if random_player.is_playing:
        return "true"
    else:
        return "false"


@app.route('/projectjimmy/api/v1.0/config/autoplay', methods=['GET'])
def toggle_random_playback():
    toggle_player()

    if random_player.is_playing:
        return "true"
    else:
        return "false"


def toggle_player():
    random_player.playable_sounds = database_helper.get_sound_list()

    values_config = ValuesConfig.ValuesConfig()
    random_player.min_interval = values_config.get_min_interval()
    random_player.max_interval = values_config.get_max_interval()

    random_player.toggle()


@app.route('/projectjimmy/api/v1.0/config/intervals/set/<interval_type>', methods=['PUT'])
def set_interval(interval_type):
    interval_hours = int(request.json.get('hours'))
    interval_minutes = int(request.json.get('minutes'))

    interval_seconds = interval_hours * 3600 + interval_minutes * 60
    values_config = ValuesConfig.ValuesConfig()
    if interval_type == "min_interval":
        values_config.set_min_interval(interval_seconds)
    elif interval_type == "max_interval":
        values_config.set_max_interval(interval_seconds)

    values_config = ValuesConfig.ValuesConfig()
    random_player.min_interval = values_config.get_min_interval()
    random_player.max_interval = values_config.get_max_interval()
    random_player.reset()

    return jsonify(request.get_json())


@app.route('/projectjimmy/api/v1.0/config/intervals/get/<interval_type>', methods=['GET'])
def get_interval(interval_type):
    values_config = ValuesConfig.ValuesConfig()

    interval_seconds = 0
    if interval_type == "min_interval":
        interval_seconds = values_config.get_min_interval()
    elif interval_type == "max_interval":
        interval_seconds = values_config.get_max_interval()

    interval_minutes, remaining_seconds = divmod(interval_seconds, 60)
    interval_hours, interval_minutes = divmod(interval_minutes, 60)

    return jsonify({'hours': interval_hours,
                    'minutes': interval_minutes})


@app.route('/projectjimmy/api/v1.0/sounds', methods=['GET'])
def get_sounds():
    return jsonify({'sounds': database_helper.sounds_table_to_json()})


@app.route('/projectjimmy/api/v1.0/sounds/setplayable/<int:sound_id>', methods=['PUT'])
def set_sound_playable(sound_id):
    is_sound_enabled = int(request.json.get('playable', 0))
    database_helper.update_sound_enabled(sound_id, is_sound_enabled)

    random_player.playable_sounds = database_helper.get_sound_list()
    random_player.reset()

    return jsonify(request.get_json())

if __name__ == "__main__":
    database_helper.create_sound_table()
    database_helper.populate_sound_table(get_file_list_in_dir(SOUNDS_PATH, EXTENSION))

    values_config_main = ValuesConfig.ValuesConfig()
    min_interval = values_config_main.get_min_interval()
    max_interval = values_config_main.get_max_interval()

    random_player = RandPlayer(database_helper.get_sound_list(), min_interval, max_interval)

    observer = Observer()
    observer.schedule(SoundFileChangeHandler(random_player), path=os.path.abspath(SOUNDS_PATH))
    observer.start()

    app.run(host='0.0.0.0', debug=True)

