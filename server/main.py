from flask import *
from flask_cors import CORS
import json
import tinydb
from tinydb import TinyDB, Query
from tinydb.operations import delete

DATABASE = 'db.json'

app = Flask(__name__)
CORS(app)

def getdb():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = TinyDB(DATABASE)
    return db

@app.teardown_appcontext
def teardown_appcontext(_):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()

@app.route('/bridge/db')
def db():
    stmt = request.args.get('stmt', None)
    db = getdb()
    q = Query()
    try:
        v = eval(stmt)
    except Exception:
        v = None
    return json.dumps(v)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, threaded=True)
