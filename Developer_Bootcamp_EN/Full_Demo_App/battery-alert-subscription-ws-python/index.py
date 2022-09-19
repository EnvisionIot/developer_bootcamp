# © 2020 Envision Digital. All Rights Reserved.

import json
import os

from dotenv import load_dotenv
from websocket import create_connection
from enos_subscribe import AlertClient

load_dotenv()

if __name__ == '__main__':
    alertClient = AlertClient(host=os.getenv('SUBSCRIPTION_HOST'),
                              port=os.getenv('SUBSCRIPTION_PORT'),
                              access_key=os.getenv('APP_ACCESS_KEY'),
                              access_secret=os.getenv('APP_ACCESS_SECRET'))
    alertClient.subscribe(sub_id=os.getenv('SUBSCRIPTION_ID'))

    uri = 'ws://' + os.getenv('WEBSOCKET_URL') + ':' + \
        os.getenv('WEBSOCKET_PORT')
    ws = create_connection(uri)
    for message in alertClient:
        msg = json.loads(message)
        msg['type'] = 'alert'
        ws.send(json.dumps(msg))
