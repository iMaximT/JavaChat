$(function(){

    var userName = 'Юзер';

    let initChat = function() {
    loadMessages();
    loadUsers();
    };

    let loadMessages = function() {
        let messagesList = $('.messages-list');
        $.get('/api/messages', function(response) {
            let messages = response.messages;
            if(messages.length == 0) {
                messagesList.append('<b> Let\'s chat</b>');
            } else {
                for(let i in messages) {
                    let messageItem = $('<div class="message"><b>' +
                        messages[i].userName + "&nbsp;" +
                        messages[i].sendTime +
                        '</b> ' + messages[i].text + '</div>');
                    messagesList.append(messageItem);
                }
            }
        });
    };

    let loadUsers = function() {
        $.get('/api/users', function(response){
            let usersList = $('.users-list');
//            let users = response.users;
            for(let i in response) {
                let userItem = $('<div class="user-item">' +
                response[i] + '</div>');
                usersList.append(userItem);
            }
        });
    };

    let authUser = function() {
        let name  = prompt('Введите имя пользователя:');
        userName = name;
        $.post('/api/users', {'name': name}, function(response){
            if(response.result){
                initChat();
            } else {
                alert('что-то не так');
            }
        });
    };

    let checkAuthStatus = function() {
        $.get('/api/auth', function(response){
            if(response.result) {
                userName = response.name;
                alert("Logged as: " + userName);
                initChat();
            } else {
            authUser();
            }
        });
    };

    checkAuthStatus();

    $('.send-message').on('click', function(){
        let message = $('.message-text').val();
        let messagesList = $('.messages-list');
        $.post('/api/messages', {'text': message}, function(response){
            if(response.result) {
                let messageItem = $('<div class="message"><b>' +
                    response.sendTime + "&nbsp;" + userName +
                    '</b> ' + message + '</div>');
                messagesList.append(messageItem);
                $('.message-text').val('');
            } else {
                alert(':(');
            }
        });
    });

});