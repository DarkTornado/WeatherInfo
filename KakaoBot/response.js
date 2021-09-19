/*
기상청 날씨 RSS 파싱
© 2021 Dark Tornado, All rights reserved.

라이선스 : Deep Dark License - Type C
라이선스 주소 : https://github.com/DarkTornado/DeepDarkLicense

이 봇은 '기상청'에서 공공누리 제1유형으로 개방한 '동네예보 RSS 서비스'을 이용하였으며,
해당 저작물은 '기상청(https://www.weather.go.kr/)에서 무료로 사용할 수 있습니다.
*/

/* 행정구역 이름을 행정구역코드로 변환 */
function getZoneId(name) {
    var data = FileStream.read("/알잘딱깔센/zone_id.csv").split("\n"); //파일 위치 알아서
    for (var n = 0; n < data.length; n++) { //일치 여부 확인
        var datum = data[n].split(",");
        if (datum[0] == name) return datum[1];
    }
    for (var n = 0; n < data.length; n++) { //포함 여부 확인
        var datum = data[n].split(",");
        if (datum[0].includes(name)) return datum[1];
    }
    return null;
}

/* 채팅 수신시 반응 */
function response(room, msg, sender, isGroupChat, replier) {
    var cmd = msg.split(" ");
    if (cmd[0] == "/날씨") {
        cmd.shift()
        var zoneId = getZoneId(cmd.join(" "));
        if (zoneId == null) {
            replier.reply("해당 위치를 찾지 못했어요");
        } else {
            var data = org.jsoup.Jsoup.connect("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=" + zoneId).get();

            /* 현재 위치 정보 */
            var location = data.select("category").text();

            /* 날씨 정보 */
            data = data.select("body").select("data");
            var days = ["오늘", "내일", "모래"];
            var results = [];
            for (var n = 0; n < data.size(); n += 2) {
                var datum = data.get(n);
                var result = " === " + days[datum.select("day").text()];
                result += " " + datum.select("hour").text() + "시 === \n";
                result += "상태 : " + datum.select("wfKor").text() + "\n";
                result += "온도 : " + datum.select("temp").text() + "℃\n";
                result += "습도 : " + datum.select("reh").text() + "%\n";
                result += "바람 : " + datum.select("wdKor").text() + "풍, " +
                    Number(datum.select("ws").text()).toFixed(1) + "m/s\n";
                result += "강수확률 : " + datum.select("pop").text() + "%";
                if (n == 0) result += "\u200b".repeat(500);
                results.push(result);
            }

            /* 날씨 정보 출력 */
            replier.reply("[" + location + " 날씨 ]\n\n" + results.join("\n\n"));
        }
    }
}