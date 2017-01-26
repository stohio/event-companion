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
        
        // if our token is valid, continue the flow of navigation
        if(checkToken())
        {
            setupSideMenu()
            let homeVC = storyboard!.instantiateViewController(withIdentifier: "MainViewController") as! MenuItem
            homeVC.menuNumber = 0
            navigationController?.pushViewController(homeVC, animated: false)
        }
        // else, request the token from the user via our user prompt
        else
        {
            userPrompt()
        }
    }
    
    /// Alert designed to only continue the navigation flow if the user authenticates their MLH account with our system.
    fileprivate func userPrompt()
    {
        let alert = UIAlertController(title: "Authentication", message: "No token found! Would you like to authenticate via your MLH account?", preferredStyle: UIAlertControllerStyle.alert)
        let tokenRequest = UIAlertAction(title: "Yes", style: UIAlertActionStyle.default)
        {
            (result : UIAlertAction) -> Void in
            self.requestToken()
        }
        let noAction = UIAlertAction(title: "No", style: UIAlertActionStyle.cancel)
        {
            (result : UIAlertAction) -> Void in
            self.userPrompt()
        }
        alert.addAction(noAction)
        alert.addAction(tokenRequest)
        self.present(alert, animated: false, completion: nil)
    }
    
    /// Directs the user to MLH authentication webpage and handles the token in AppDelegate.swift
    fileprivate func requestToken()
    {
        let url = URL(string: "https://my.mlh.io/oauth/authorize?client_id=664be6f8c0d9f8098c83f56454c3fa5abfe507514d8304b044163ff8b3cfb783&redirect_uri=https%3A%2F%2Fstoh.io%2Foauth%2Fcallback.html&response_type=token")!
        if UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }
    }
    
    /// Determines whether or not the token exists wihin our secure KeyChain.
    fileprivate func checkToken() -> Bool
    {
        let keychain = KeychainSwift()
        keychain.synchronizable = true
        let token = keychain.get("token")
        if(token != nil)
        {
//            keychain.clear()
            return true
        }
        return false
    }
    
    /// Setting up our required options for our side menu.
    fileprivate func setupSideMenu() {
        // Define the menu
        SideMenuManager.menuLeftNavigationController = storyboard!.instantiateViewController(withIdentifier: "LeftMenuNavigationController") as? UISideMenuNavigationController
        
        // Enable gestures and customize view and functionality
        SideMenuManager.menuAddPanGestureToPresent(toView: self.navigationController!.navigationBar)
        SideMenuManager.menuAddScreenEdgePanGesturesToPresent(toView: self.navigationController!.view)
        SideMenuManager.menuFadeStatusBar = false
        SideMenuManager.menuPushStyle = .preserveAndHideBackButton
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

