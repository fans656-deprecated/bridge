from flask import *
from flask_cors import CORS
import sqlite3

import json

DATABASE = 'data.db'

app = Flask(__name__)
CORS(app)

def init_db():
    with app.app_context():
        conn = getdb()
        c = conn.cursor()
        c.execute('create table pages (name text, content text)')
        conn.commit()

def getdb():
    db = getattr(g, '_database', None)
    if db is None:
        db = g._database = sqlite3.connect(DATABASE)
    return db

@app.teardown_appcontext
def teardown_appcontext(_):
    db = getattr(g, '_database', None)
    if db is not None:
        db.close()

def all_db_content():
    r = list(getdb().cursor().execute('select * from pages'))
    return json.dumps(r)

@app.route('/bridge/query')
def query():
    name = request.args.get('name', None)
    print u'query "{}"'.format(name)
    if name is None:
        print 'query all'
        return all_db_content()
    else:
        conn = getdb()
        c = conn.cursor()
        r = list(
            c.execute('select content from pages where name = ?', (name,)))
        if r:
            print 'query content ok'
            return r[0]
        else:
            s = u'Error: no name {}'.format(name)
            print s
            return s

@app.route('/bridge/insert')
def insert():
    name = request.args.get('name', None)
    if name is None:
        return 'Error: name can not be None'
    content = request.args.get('content', '')
    conn = getdb()
    c = conn.cursor()
    if list(c.execute('select * from pages where name = ?', (name,))):
        c.execute('update pages set content = ? where name = ?',
                              (content, name))
    else:
        c.execute('insert into pages values (?, ?)', (name, content))
    conn.commit()
    return 'OK'

@app.route('/bridge/delete')
def delete():
    name = request.args.get('name', None)
    if name is None:
        return 'Error: no name supplied'
    conn = getdb()
    c = conn.cursor()
    c.execute('delete from pages where name = ?', (name,))
    conn.commit()
    return 'OK'

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80)
