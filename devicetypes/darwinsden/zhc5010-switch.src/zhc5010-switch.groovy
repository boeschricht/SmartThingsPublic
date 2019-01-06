/**
 *  ZHC5010 Z-Wave switch module test
 *
 *  Copyright 2018 Bo Eschricht. Based on original work by DarwinsDen.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Author: boeschricht@gmail.com
 *	Date: 2019-01-06
 *
 *	Changelog:
 *
 *  0.1 (02/07/2017, boeschricht@gmail.com) -  Major improvements of handler code for firmware 1.10.
 *                                              Improved Simulator support
 *                                              Fixes:
 *                                              Added support for command classes:
 *                                               - Central Scene - for better handling of button double-press, hold
 *                                               - Multi-level - dimming
 *                                               - Indicator - for control of LED's
 *                                              Added preference options for:
 *                                               - Enhanced control of relay mode
 *				                        		           - Control of LED indicator modes, LED "on"/"off" brightness levels
 *                                               - Disabling House Cleaning Mode (double press sends dimming command for 100% light level)
 *
 */
 */


metadata {
	definition (name: "ZHC5010v2", namespace: "darwinsden", author: "darwin@darwinsden.com") {
		capability "Actuator"
		capability "Switch"
        capability "Button"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
        capability "Configuration"
		capability "Indicator"



        //fingerprint deviceId: "0x1001", inClusters: "0x5E, 0x86, 0x72, 0x5A, 0x85, 0x59, 0x73, 0x25, 0x27, 0x70, 0x2C, 0x2B, 0x5B, 0x7A", outClusters: "0x5B"
}
	// simulator metadata
	simulator {
	}

    preferences {
       input "doublePressCancelsSingle", "bool", title: "Cancel Single-Press when followed by Double-Press",  defaultValue: false,  displayDuringSetup: true, required: false
       input "SwitchRelayButtonNo", "number", title: "Physical relay mapped to button number (1-4)",  defaultValue: 1,  range: "1..4", displayDuringSetup: true, required: true
       input "disableSwitchRelay", "bool", title: "Disable the switch physical relay",  defaultValue: false,  displayDuringSetup: true, required: true
       input "disableLED1", "bool", title: "Disable LED #1",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED1BriOn", "number", title: "LED1 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED1BriOff", "number", title: "LED1 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
       input "disableLED2", "bool", title: "Disable LED #2",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED2BriOn", "number", title: "LED2 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED2BriOff", "number", title: "LED2 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
       input "disableLED3", "bool", title: "Disable LED #3",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED3BriOn", "number", title: "LED3 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED3BriOff", "number", title: "LED3 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
       input "disableLED4", "bool", title: "Disable LED #4",  defaultValue: true,  displayDuringSetup: true, required: true
       input "LED4BriOn", "number", title: "LED4 brightness on",  defaultValue: 50, range:"0..100", displayDuringSetup: true, required: true
       input "LED4BriOff", "number", title: "LED4 brightness off",  defaultValue: 0, range:"0..100", displayDuringSetup: true, required: true
    }
//	tiles(scale: 2) {
// Removed AP start
//      	valueTile("buttonTile", "device.buttonNum", width: 2, height: 2) {
// 			state("", label:'${currentValue}')
// 		}
// 		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
// 			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
// 		}
//
// 		main "buttonTile"
// 		details(["buttonTile","refresh"])
// Removed AP slut



// Added AP start
	tiles {
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.unknown.zwave.device", backgroundColor: "#00A0DC"
			state "off", label: '${name}', action: "switch.on", icon: "st.unknown.zwave.device", backgroundColor: "#ffffff"
		}
		standardTile("switchOn", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "on", label:'on', action:"switch.on", icon:"st.switches.switch.on"
		}
		standardTile("switchOff", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "off", label:'off', action:"switch.off", icon:"st.switches.switch.off"
		}
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
			state "level", action:"switch level.setLevel"
		}



		main "switch"
		details (["switch", "switchOn", "switchOff", "levelSliderControl", "refresh"])
// Added AP slut
	}
}
def parse(String description) {
	log.debug("description: ${description}")
 	def result = null
 	def cmd = zwave.parse(description, [0x20: 1, 0x70: 1])

    if (cmd) {
  		result = zwaveEvent(cmd)
  	}
    if (!result){
        log.debug "Parse returned ${result} for command ${cmd}"
    } else {
  		log.debug "Parse returned ${result}"
    }
 	return result
}
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
  	createEvent([name: "switch", value: cmd.value ? "on" : "off", type: "physical"])
}
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
  	createEvent([name: "switch", value: cmd.value ? "on" : "off", type: "physical"])
}
def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
 	createEvent([name: "switch", value: cmd.value ? "on" : "off", type: "digital"])
}
def zwaveEvent(physicalgraph.zwave.commands.hailv1.Hail cmd) {
 	createEvent([name: "hail", value: "hail", descriptionText: "Switch button was pressed", displayed: false])
}
def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
  	if (state.manufacturer != cmd.manufacturerName) {
 		createEvent(updateDataValue("manufacturer", cmd.manufacturerName))
 	}
}
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	// Handles all Z-Wave commands we aren't interested in
    log.debug (cmd)
    createEvent([:])
}
def pressedButton (def btnRes) {

  def canceling = false

  if (state.doublePressed1 && btnRes ==1) {
     canceling = true
     state.doublePressed1 = false
  }
  else if (state.doublePressed2 && btnRes == 2) {
     canceling = true
     state.doublePressed2 = false
  }
  else if (state.doublePressed3 && btnRes == 3) {
     canceling = true
     state.doublePressed3 = false
  }
  else if (state.doublePressed4 && btnRes == 4) {
     canceling = true
     state.doublePressed4 = false
  }

  if (canceling) {
         log.debug ("Canceling single press for button $btnRes")
         state.doublePressed=false
  }
  else
     {
         log.debug ("button $btnRes pushed")
         sendEvent(name: "buttonNum" , value: "Btn: $btnRes pushed")
         sendEvent([name: "button", value: "pushed", data: [buttonNumber: "$btnRes"], descriptionText: "$device.displayName $btnRes pressed", isStateChange: true, type: "physical"])
       }
}
def pressedButton1() {
   pressedButton (1)
}
def pressedButton2() {
   pressedButton (2)
}
def pressedButton3() {
   pressedButton (3)
}
def pressedButton4() {
   pressedButton (4)
}
def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
    log.debug("sceneNumber: ${cmd.sceneNumber} keyAttributes: ${cmd.keyAttributes}")
    def result = []
    switch (cmd.keyAttributes) {
       case 0:
           //pressed
           def buttonResult = cmd.sceneNumber
           if (doublePressCancelsSingle)
           {
             switch (buttonResult) {
               case 1:
                  state.doublePressed1=false
                  runIn (1, pressedButton1)
                  break
               case 2:
                  state.doublePressed2=false
                  runIn (1, pressedButton2)
                  break
               case 3:
                  state.doublePressed3=false
                  runIn (1, pressedButton3)
                  break
               case 4:
                  state.doublePressed4=false
                  runIn (1, pressedButton4)
                  break
               default:
                 log.debug ("unexpected button $buttonNum")
             }
           }
           else
           {
				sendEvent(name: "buttonNum" , value: "Btn: $buttonResult pushed")
             	result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"],
                	descriptionText: "$device.displayName $buttonResult pressed", isStateChange: true, type: "physical"])
           }

           // if button pressed is button for physical relay, then toggle switch state
           if (! disableSwitchRelay) {
           		log.debug("SwitchRelayButtonNo: ${SwitchRelayButtonNo}")
                log.debug("buttonResult: ${buttonResult}")
                if ($SwitchRelayButtonNo == $buttonResult) { // button for physical relay pressed
                	log.debug("switch.currentSwitch: ${device.currentValue('switch')}")
                    if (device.currentValue('switch') == "on") {
	                    sendEvent(name: "switch" , value: "off")
                    } else {
	                    sendEvent(name: "switch" , value: "on")
                    }
                }
            }

            break
// xxx

       case 1:
           //released
           def buttonResult = cmd.sceneNumber
           sendEvent(name: "buttonNum" , value: "Btn: $buttonResult released")
           result=createEvent([name: "button", value: "released", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult released", isStateChange: true, type: "physical"])
           break

       case 2:
           //held
           def buttonResult = cmd.sceneNumber
           result=createEvent([name: "button", value: "held", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult held", isStateChange: true, type: "physical"])
           break

       case 3:
           //double press
           def buttonResult = cmd.sceneNumber + 4

           switch (buttonResult) {
           case 5:
              state.doublePressed1=true
              break
           case 6:
              state.doublePressed2=true
              break
           case 7:
              state.doublePressed3=true
              break
           case 8:
              state.doublePressed4=true
              break
           default:
              log.debug ("unexpected double press button: $buttonResult")
           }

           sendEvent(name: "buttonNum" , value: "Btn: $buttonResult double press")
           result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult double-pressed", isStateChange: true, type: "physical"])
           break
       case 4:
           //triple press -- not currently supported
           log.debug("tripple press")
           def buttonResult = cmd.sceneNumber + 8
           sendEvent(name: "buttonNum" , value: "Btn: $buttonResult double press")
           result=createEvent([name: "button", value: "pushed", data: [buttonNumber: "$buttonResult"],
                         descriptionText: "$device.displayName $buttonResult double-pressed", isStateChange: true, type: "physical"])
           break
      default:
           // unexpected case
           log.debug ("unexpected attribute: $cmd.keyAttributes")
   }
   return result
}



// Added AP start
def on() {
//    def cmds = []
//    cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 3, size: 1).format()
//    delayBetween(cmds, 500)
	commands([zwave.basicV1.basicSet(value: 0xFF), zwave.basicV1.basicGet()])


}



def off() {
	commands([zwave.basicV1.basicSet(value: 0x00), zwave.basicV1.basicGet()])
}



def setLevel(value) {
	commands([zwave.basicV1.basicSet(value: value as Integer), zwave.basicV1.basicGet()], 4000)
}
// Added AP slut



def configure() {
     sendEvent(name: "numberOfButtons", value: 12, displayed: false)

    def cmds = []
    cmds << zwave.configurationV1.configurationSet(configurationValue: disableLED1 ? [0] : [1], parameterNumber: 3, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: disableLED2 ? [0] : [1], parameterNumber: 4, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: disableLED3 ? [0] : [1], parameterNumber: 5, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: disableLED4 ? [0] : [1], parameterNumber: 6, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED1BriOn], parameterNumber: 7, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED2BriOn], parameterNumber: 8, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED3BriOn], parameterNumber: 9, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED4BriOn], parameterNumber: 10, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED1BriOff], parameterNumber: 11, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED2BriOff], parameterNumber: 12, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED3BriOff], parameterNumber: 13, size: 1 )
    cmds << zwave.configurationV1.configurationSet(configurationValue: [LED4BriOff], parameterNumber: 14, size: 1 )
   if (disableSwitchRelay) {
    	cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 15, size: 1 )
    } else {
    	cmds << zwave.configurationV1.configurationSet(configurationValue: [SwitchRelayButtonNo], parameterNumber: 15, size: 1 )
    }
    commands(cmds, 1000)
}
def refresh() {
  configure()
}



// Added AP start
private command(physicalgraph.zwave.Command cmd) {
	if (state.sec) {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}



private commands(commands, delay=200) {
	delayBetween(commands.collect{ command(it) }, delay)
}
// Added AP slut
