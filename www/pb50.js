var exec = require('cordova/exec');

exports.print = function(mac, content, success, error) {
    exec(success, error, "pb50", "print", [mac, content]);
}