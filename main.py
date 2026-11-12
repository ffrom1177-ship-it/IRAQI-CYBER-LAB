
from kivy.app import App
from kivy.lang import Builder
from kivy.core.window import Window
from kivy.uix.screenmanager import ScreenManager, Screen

Window.clearcolor = (0.035, 0.045, 0.07, 1)

KV = '''
ScreenManager:
    Screen:
        name: "home"

        BoxLayout:
            orientation: "vertical"
            padding: "18dp"
            spacing: "14dp"

            BoxLayout:
                size_hint_y: None
                height: "65dp"
                orientation: "vertical"

                Label:
                    text: "[b]IRAQI CYBER LAB[/b]"
                    markup: True
                    font_size: "24sp"
                    color: 0.1, 0.9, 0.7, 1

                Label:
                    text: "مختبر الأمن السيبراني التعليمي"
                    font_size: "14sp"
                    color: 0.7, 0.8, 0.9, 1

            Label:
                text: "أهلاً بك في مختبرك الأمني 🇮🇶"
                font_size: "19sp"
                size_hint_y: None
                height: "55dp"

            Button:
                text: "🔍  فحص المنافذ المحلي"
                on_release: app.show_info("فحص المنافذ", "قريباً: فحص المنافذ على جهازك المحلي.")
                background_normal: ""
                background_color: 0.05, 0.35, 0.32, 1

            Button:
                text: "🌐  تحليل DNS"
                on_release: app.show_info("تحليل DNS", "قريباً: تحليل أسماء النطاقات.")
                background_normal: ""
                background_color: 0.05, 0.35, 0.32, 1

            Button:
                text: "🛡️  تحليل HTTP"
                on_release: app.show_info("تحليل HTTP", "قريباً: عرض استجابات HTTP.")
                background_normal: ""
                background_color: 0.05, 0.35, 0.32, 1

            Button:
                text: "📚  دروس الأمن السيبراني"
                on_release: app.show_info("الدروس", "تعلم Linux وPython والشبكات والأمن السيبراني.")
                background_normal: ""
                background_color: 0.05, 0.35, 0.32, 1

            Widget:

            Label:
                text: "IRAQI CYBER LAB | الإصدار 1.0"
                size_hint_y: None
                height: "35dp"
                font_size: "12sp"
                color: 0.5, 0.6, 0.7, 1
'''

class CyberApp(App):

    def build(self):
        return Builder.load_string(KV)

    def show_info(self, title, message):
        from kivy.uix.popup import Popup
        from kivy.uix.label import Label

        Popup(
            title=title,
            content=Label(text=message),
            size_hint=(0.85, 0.35)
        ).open()

CyberApp().run()

