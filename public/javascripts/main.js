$(document).ready(function () {

  var sign = function (action, data) {
    var jqxhr = $.ajax({
      method: "POST",
      url: action,
      data: data
    }).done(function () {
      window.location.href = '/';
    }).fail(function () {
      alert("error");
    })
  };

  $('#signin').click(function () {
    sign("/signin", {email: $('#email').val(), password: $('#password').val()});
  });

  $('#signup').click(function () {
    sign("/signup", {email: $('#email').val(), password: $('#password').val()});
  });

  $('input[type=password]').keyup(function () {
    var pswd = $(this).val();

    //validate the length
    if (pswd.length < 8) {
      $('#length').removeClass('valid').addClass('invalid');
    } else {
      $('#length').removeClass('invalid').addClass('valid');
    }

    //validate letter
    if (pswd.match(/[A-z]/)) {
      $('#letter').removeClass('invalid').addClass('valid');
    } else {
      $('#letter').removeClass('valid').addClass('invalid');
    }

    //validate capital letter
    if (pswd.match(/[A-Z]/)) {
      $('#capital').removeClass('invalid').addClass('valid');
    } else {
      $('#capital').removeClass('valid').addClass('invalid');
    }

    //validate number
    if (pswd.match(/\d/)) {
      $('#number').removeClass('invalid').addClass('valid');
    } else {
      $('#number').removeClass('valid').addClass('invalid');
    }

    //validate space
    if (pswd.match(/[^a-zA-Z0-9\-\/]/)) {
      $('#space').removeClass('invalid').addClass('valid');
    } else {
      $('#space').removeClass('valid').addClass('invalid');
    }

  }).focus(function () {
    $('#pswd_info').show();
  }).blur(function () {
    $('#pswd_info').hide();
  });

});