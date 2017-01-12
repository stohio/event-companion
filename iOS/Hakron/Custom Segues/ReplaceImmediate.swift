//
//  ReplaceImmediate.swift
//  Hakron
//
//  Created by Aron Gates on 1/9/17.
//  Copyright © 2017 Aron Gates. All rights reserved.
//
//  This custom segue rearranges the stack so that the destination controller
//  will rise to the top of the stack, and makes certain that the other items
//  in the stack will remain unchanged. 
//  TL;DR we rearrange the stack

//  If you don't want each view controller's state to be saved, and instead 
//  want a new instance of each view, remove everything within the first if block
//  with this line of code:
//  presentingNav.setViewControllers([destination], animated: false)

import UIKit
import SideMenu

class ReplaceImmediate: UIStoryboardSegue {
    override func perform() {
        let source = self.source
        let destination = self.destination
        
        if let nav = source.navigationController as? UISideMenuNavigationController,
            let presentingNav = nav.presentingViewController as? UINavigationController {
            presentingNav.dismiss(animated: true, completion: nil)
            
            // if the destination controller is already in stack, we will bring it
            // to the front of the stack while maintaining the items that are already
            // in the stack
            if(isInStack(navController: presentingNav, viewController: destination))
            {
                var storedController:[UIViewController] = []
                var theController:UIViewController!
                for controller in presentingNav.viewControllers {
                    if(controller.nibName == destination.nibName)
                    {
                        theController = controller
                    }
                    else
                    {
                        storedController.append(controller)
                    }
                }
                storedController.append(theController)
                presentingNav.popToRootViewController(animated: false)
                
                for controller in storedController {
                    if(isInStack(navController: presentingNav, viewController: controller))
                    {
                        continue
                    }
                    presentingNav.pushViewController(controller, animated: false)
                }
            }
            // if the destination controller is not in the stack, we will
            // simple push it to the stack
            else
            {
                presentingNav.pushViewController(destination, animated: false)
            }
        }
        
    }
    
    // simple function to determine whether the viewcontroller is in the stack or not
    private func isInStack(navController: UINavigationController, viewController: UIViewController) -> Bool
    {
        for controller in navController.viewControllers {
            if(controller.nibName == viewController.nibName)
            {
                return true
            }
        }
        return false
    }
}

