var app = {
    server_base_url: '23.83.243.37/bridge',
    //server_base_url: 'localhost/bridge',
    initialize: function() {
        this.bindEvents();
    },
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    onDeviceReady: function() {
        console.log('onDeviceReady');
        app.queryList();
        $('.back').click(function() {
            $.mobile.back();
        });
        $('#update-list').click(function() {
            console.log('queryList');
            app.queryList();
        });
        $('#update-content').click(function() {
            app.queryContent(app.current_page);
        });
        $('#option-server-url').val(app.server_base_url);
        $('#option-ok').click(function() {
            if ($('#option-local')[0].checked) {
                app.server_base_url = 'localhost/bridge';
                console.log('set server to ' + app.server_base_url);
            } else {
                app.server_base_url = $('#option-server-url').val();
                console.log('set server to ' + app.server_base_url);
            }
            $.mobile.back();
        });
    },
    queryList: function() {
        $.mobile.loading('show');
        $.ajax({
            url: 'http://' + app.server_base_url + '/query'
        }).done(function(data) {
            pages = JSON.parse(data);
            var s = '';
            var dummy = $('<ul>');
            $.each(pages, function(idx, val) {
                var name = val[0];
                var a = $('<a>', {
                    text: name,
                    href: '#content-page',
                    click: function() {
                        app.current_page = name;
                        app.queryContent(name);
                        console.log('click query ' + name);
                    },
                });
                var li = $('<li>');
                a.appendTo(li);
                li.appendTo(dummy);
            });
            var ul = $('#list');
            ul.empty();
            dummy.children().appendTo(ul);
            ul.listview('refresh');

            var currentdate = new Date(); 
            var datetime = "" + currentdate.getDate() + "/"
                + (currentdate.getMonth()+1)  + "/" 
                + currentdate.getFullYear() + " @ "  
                + currentdate.getHours() + ":"  
                + currentdate.getMinutes() + ":" 
            + currentdate.getSeconds();
            var s = 'Updated on ' + datetime
            console.log(s);

            $.mobile.loading('hide');
            }).error(function() {
                $.mobile.loading('hide');
            });
    },
    queryContent: function(name) {
        $.mobile.loading('show');
        $('#content').val('');
        $.ajax({
            url: 'http://' + app.server_base_url + '/query',
            data: {name: name},
        }).done(function(data) {
            console.log('content:' + data);
            $('#content').val(data).refresh();
            $.mobile.loading('hide');
        }).error(function() {
            $.mobile.loading('hide');
        });
    },
};

app.initialize();

$(function() {
    if (navigator.userAgent.match(/(iPhone|iPod|iPad|Android|BlackBerry)/)) {
        document.addEventListener("deviceready", app.onDeviceReady, false);
        console.log('linsener added');
    } else {
        app.onDeviceReady();
    }
});
