import config

import requests

import time
import os
import cPickle as pickle

os.environ['NO_PROXY'] = config.server

prev = {}
now = {}

pkfile = 'prev.pk'
if not os.path.exists(pkfile):
    with open('prev.pk', 'w') as f:
        pickle.dump(prev, f)
with open('prev.pk') as f:
    prev = pickle.load(f)

pkfile = open(pkfile, 'r+')

def check_file(path):
    global prev, now
    r = os.stat(path)
    now[path] = r.st_mtime
    if path not in prev or now[path] > prev[path]:
        print u'uploading {}'.format(path)
        with open(path) as f:
            content = f.read()
        r = requests.get(config.server_url + '/insert', params={
            'name': os.path.basename(path),
            'content': content,
        })
        if r.text == 'OK':
            prev[path] = now[path]
            print u'OK {}'.format(path)
        else:
            print u'Error {}'.format(path)
            print u'Return "{}"'.format(r.text)

while True:
    time.sleep(1)

    for path in config.paths:
        if not os.path.exists(path):
            continue
        if os.path.isdir(path):
            dirpath = path
            for dirpath, dirnames, fnames in os.walk(dirpath):
                for fname in fnames:
                    path = os.path.join(dirpath, fname)
                    check_file(path)
        else:
            check_file(path)
    pickle.dump(prev, pkfile)
    paths = list(config.paths)
    reload(config)
    if config.paths != paths:
        print 'paths changed to: {}'.format(config.paths)
