from machine import Pin

joystick_left = [
    Pin(2, Pin.IN, Pin.PULL_UP), # up
    Pin(3, Pin.IN, Pin.PULL_UP), # down
    Pin(4, Pin.IN, Pin.PULL_UP), # left
    Pin(5, Pin.IN, Pin.PULL_UP)  # right
]

while True:
    inputs = [0] * 4

    for i in range(len(joystick_left)):
        inputs[i] = 1 - joystick_left[i].value() # normally open

    print(f"P:{''.join(str(int(b)) for b in inputs)}")
