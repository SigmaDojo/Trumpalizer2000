const datasrc = "";

const users = [
	{"username": "realDonaldTrump", "display": "President Trump"},
	{"username": "barackobama", "display": "President Emeritus Obama"},
	{"username": "andonilsson", "display": "Anders Nilsson"},
	{"username": "theexcellentnin", "display": "Johan Fogelström"},
	{"username": "martintiinus", "display": "Martin Tiinus"},
	{"username": "fredrikalserin", "display": "Fredrik Alserin"},
];

const methods = [
	{"value": "byDay", "name": "Veckodag"},
	{"value": "byHour", "name": "Timme"},
];

function populateUsers(select) {
	for (const i in users) {
		select.append(`<option value="${users[i].username}">${users[i].display}</option>"`)
	}
}

function populateMethods(select) {
	for (const i in methods) {
		select.append(`<option value="${methods[i].value}">${methods[i].name}</option>"`)
	}
}

function getData(target, method, callback) {
	d3.json(datasrc + `/timeline/${target}/${method}`, callback);
	//d3.json(datasrc + "/data", callback);
}

function updateProfile(user) {
	$("img.profile-photo").attr('src', user.profile_image_url);
	$(".profile-name").text(user.name);
}

function c3data(data) {
	return {
		type: 'bar',
		json: data,
		keys: {
			x: 'x', // it's possible to specify 'x' when category axis
			value: ['y']
		},
		names: {
			y: '# of tweets'
		}
	}
}

function diagram(identifier, data) {
	var chart = c3.generate({
		bindto: identifier,
		data: c3data(data)
	});

	return  function(data) {
		chart.load(c3data(data));
	}
}

function main() {
	populateUsers($(".controls #target"));
	populateMethods($(".controls #method"));

	let fetch = function(callback) {
		getData(
			$(".controls #target").val(),
			$(".controls #method").val(),
			callback);
	}

	fetch(function (res) {
		updateProfile(res.user)
		let updater = diagram('#diagram', res.data);
		$(".controls form").change(function () {
			fetch(function (res) {
				updateProfile(res.user)
				updater(res.data);
			});
		});
	});
}

main();
