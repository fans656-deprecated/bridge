from flask import *

app = Flask(__name__, static_url_path='', static_folder='.')
app.add_url_rule('/<path:filename>',
                 endpoint='',
                 subdomain='',
                 view_func=app.send_static_file
                 )

@app.route('/')
def index():
    print 'hi'
    return send_from_directory('.', 'index.html')

if __name__ == '__main__':
    app.run(debug=True)
