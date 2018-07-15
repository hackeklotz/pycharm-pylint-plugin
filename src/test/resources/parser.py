from typing import Dict, List
import re
import html
import logging

import lxml.html

from lxml import etree

from crime import MediaInformation
from crime import CrimeOriginal

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)


class Parser:
    def parse_reports(self, pages: Dict[str, str]) -> List[MediaInformation]:
        reports = []
        for url, text in pages.items():
            tree = lxml.html.fromstring(text)

            if self.__is_police_district_dresden(tree):
                crimes = self.__parse_crimes(tree)

                url = url[:-4]
                report_id = url.rpartition('_')[2]

                report = MediaInformation(report_id, 2017, "", "title", "date", crimes)
                reports.append(report)

        return reports

    def __is_police_district_dresden(self, tree):
        title = tree.xpath('//title')[0].text
        return "Dresden" in title

    def __parse_crimes(self, tree):
        content = tree.xpath('//div[@id="content"]')
        div = content[0].getchildren()[6]

        textbytes = etree.tostring(div)
        text = str(textbytes, 'utf-8')
        text = html.unescape(text)
        parts = re.split("<a name=[\"\']?#mi.*?></a>", text)

        crimes = []
        for part in parts:
            if self.__iscrime(part):
                crime = self.__parse_single_crime(part)
                crimes.append(crime)

        return crimes

    def __iscrime(self, text):
        text = self.__cleanfromtags(text)

        if text.isspace() or not text:
            return False

        if (text == "Landeshauptstadt Dresden"
            or text == "Landkreis Meißen"
            or text == "Landkreis Sächsische Schweiz-Osterzgebirge"):
            return False

        if text.strip().startswith("Medieninformation"):
            return False

        if text.strip().startswith("Wer hat den Unfall beobachtet"):
            return False

        return True

    def __cleanfromtags(self, text):
        return re.sub("<.*?>", "", text)

    def __parse_single_crime(self, part):
        line_break = "(?:<br />|<BR>)"
        match = re.match("(.*?)Zeit\s?:(.*)?Ort\s?:(.*?)" + line_break + "(.*)", part)
        if match:
            title = self.__cleantext(match.group(1))
            date = self.__cleantext(match.group(2))
            place = self.__cleantext(match.group(3))
            message = self.__cleantext(match.group(4))

            return CrimeOriginal(title, date, place, message)

        match = re.match("(.*?)Ort\s?:(.*)?Zeit\s?:(.*?)" + line_break + "(.*)", part)
        if match:
            title = self.__cleantext(match.group(1))
            date = self.__cleantext(match.group(3))
            place = self.__cleantext(match.group(2))
            message = self.__cleantext(match.group(4))

            return CrimeOriginal(title, date, place, message)

        match = re.match("(.*?)Tatzeit\s?:(.*)?Tatort\s?:(.*?)" + line_break + "(.*)", part)
        if match:
            title = self.__cleantext(match.group(1))
            date = self.__cleantext(match.group(2))
            place = self.__cleantext(match.group(3))
            message = self.__cleantext(match.group(4))

            return CrimeOriginal(title, date, place, message)

        match = re.match("(.*?)" + line_break + "(.*)", part)
        if match:
            title = self.__cleantext(match.group(1))
            message = self.__cleantext(match.group(2))
            return CrimeOriginal(title, None, None, message)

        logging.warning("Couldn't match a crime")
        logging.debug("Couldn't match the crime" + part)
        return CrimeOriginal("", "", "", "")

    def __cleantext(self, text):
        clean_text = text.replace("<br /><br />", "\n")
        clean_text = self.__cleanfromtags(clean_text)
        return clean_text.strip()


def main():
    import json

    with open('../examples/crimes2017.json') as data_file:
        pages = json.load(data_file)

    parser = Parser()
    parser.parse_reports(pages)


if __name__ == "__main__":
    main()
