//
//  InitViewController.swift
//  Hakron
//
//  Created by Aron Gates on 1/9/17.
//  Copyright © 2017 Aron Gates. All rights reserved.
//

import KeychainSwift
import SideMenu
import UIKit

class InitViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()

        if(checkToken())
        {
            setupSideMenu()
            let homeVC = storyboard!.instantiateViewController(withIdentifier: "MainViewController") as! MenuItem
            homeVC.menuNumber = 0
            navigationController?.pushViewController(homeVC, animated: false)
        }
        else
        {
            let url = URL(string: "https://my.mlh.io/oauth/authorize?client_id=664be6f8c0d9f8098c83f56454c3fa5abfe507514d8304b044163ff8b3cfb783&redirect_uri=http%3A%2F%2Fstoh.io%2Foauth%2Fcallback.html&response_type=token")!
            if UIApplication.shared.canOpenURL(url) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
                //If you want handle the completion block than
                UIApplication.shared.open(url, options: [:], completionHandler: { (success) in
                    print("Open url : \(success)")
                })
            }
        }
    }
    
    fileprivate func checkToken() -> Bool
    {
        let keychain = KeychainSwift()
        keychain.synchronizable = true
        let token = keychain.get("token")
        if(token != nil)
        {
            keychain.clear()
            return true
        }
        return false
    }
    
    fileprivate func setupSideMenu() {
        // Define the menus
        SideMenuManager.menuLeftNavigationController = storyboard!.instantiateViewController(withIdentifier: "LeftMenuNavigationController") as? UISideMenuNavigationController
        
        // Enable gestures. The left and/or right menus must be set up above for these to work.
        // Note that these continue to work on the Navigation Controller independent of the View Controller it displays!
        SideMenuManager.menuAddPanGestureToPresent(toView: self.navigationController!.navigationBar)
        SideMenuManager.menuAddScreenEdgePanGesturesToPresent(toView: self.navigationController!.view)
        SideMenuManager.menuReplaceOnPush = false
        SideMenuManager.menuFadeStatusBar = false

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

