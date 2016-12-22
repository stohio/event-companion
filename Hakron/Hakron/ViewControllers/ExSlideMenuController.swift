//
//  ExSlideMenuController.swift
//  SlideMenuControllerSwift
//
//  Created by Yuji Hato on 11/11/15.
//  Copyright © 2015 Yuji Hato. All rights reserved.
//

import UIKit
import SlideMenuControllerSwift

class ExSlideMenuController : SlideMenuController {

    override func isTagetViewController() -> Bool {
        if UIApplication.topViewController() != nil {
            return true
        }
        return false
    }
    
}
