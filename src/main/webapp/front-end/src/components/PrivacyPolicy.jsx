import React from 'react';
import { Grid, Jumbotron, Button, Glyphicon } from 'react-bootstrap';
import { Link, Redirect } from 'react-router-dom';
import BeeLabel from './BeeLabel.jsx';
import NavBar from './NavBar.jsx';

export default class PrivacyPolicy extends React.Component {
  render() {
    return <Grid>
        <NavBar inverse={true} />

        <h3>Treople Privacy Policy</h3>
        <div>
            <div className='ppConsistencies'>
                <div className='col-2'>
                    <div className="quick-links text-center">Information Collection</div>
                </div>
                <div className='col-2'>
                    <div className="quick-links text-center">Information Usage</div>
                </div>
                <div className='col-2'>
                    <div className="quick-links text-center">Information Protection</div>
                </div>
                <div className='col-2'>
                    <div className="quick-links text-center">Cookie Usage</div>
                </div>
                <div className='col-2'>
                    <div className="quick-links text-center">3rd Party Disclosure</div>
                </div>
                <div className='col-2'>
                    <div className="quick-links text-center">3rd Party Links</div>
                </div>
                <div className='col-2'>
                </div>
            </div>
            <div className="styled-separator"></div>
            <div className='ppConsistencies'>
                <div className='col-2'>
                    <div className="col-12 quick-links2 gen-text-center">Google AdSense</div>
                </div>
                <div className='col-2'>
                    <div className="col-12 quick-links2 gen-text-center">Fair Information Practices
                        <div className="col-8 gen-text-left gen-xs-text-center fair-information-div" >Fair information<br /> Practices</div>
                    </div>
                </div>
                <div className='col-2'>
                    <div className="col-12 quick-links2 gen-text-center coppa-pad">COPPA</div>
                </div>
                <div className='col-2'>
                    <div className="col-12 quick-links2 quick4 gen-text-center caloppa-pad">CalOPPA</div>
                </div>
                <div className='col-2'>
                    <div className="quick-links2 gen-text-center">Our Contact Information<br /></div>
                </div>
            </div>
            <div className="styled-separator"></div>
            <div className='innerText'>This privacy policy has been compiled to better serve those who are concerned with how their 'Personally Identifiable Information' (PII) is being used online. PII, as described in US privacy law and information security, is information that can be used on its own or with other information to identify, contact, or locate a single person, or to identify an individual in context. Please read our privacy policy carefully to get a clear understanding of how we collect, use, protect or otherwise handle your Personally Identifiable Information in accordance with our website.<br /></div>
            <span id='infoCo'></span>
            <br />
            <div className='grayText'><strong>What personal information do we collect from the people that visit our blog, website or app?</strong></div>
            <br />
            <div className='innerText'>When ordering or registering on our site, as appropriate, you may be asked to enter your email address  or other details to help you with your experience.</div>
            <br />
            <div className='grayText'><strong>When do we collect information?</strong></div>
            <br />
            <div className='innerText'>We collect information from you when you register on our site or enter information on our site.</div>
            <br />
            <span id='infoUs'></span>
            <br />
            <div className='grayText'><strong>How do we use your information? </strong></div>
            <br />
            <div className='innerText'> We may use the information we collect from you when you register, make a purchase, sign up for our newsletter, respond to a survey or marketing communication, surf the website, or use certain other site features in the following ways:<br /><br /></div>
            <span id='infoPro'></span>
            <br />
            <div className='grayText'><strong>How do we protect your information?</strong></div>
            <br />
            <div className='innerText'>We do not use vulnerability scanning and/or scanning to PCI standards.</div>
            <div className='innerText'>We only provide articles and information. We never ask for credit card numbers.</div>
            <div className='innerText'>We do not use Malware Scanning.<br /><br /></div>
            <div className='innerText'>Your personal information is contained behind secured networks and is only accessible by a limited number of persons who have special access rights to such systems, and are required to keep the information confidential. In addition, all sensitive/credit information you supply is encrypted via Secure Socket Layer (SSL) technology. </div>
            <br />
            <div className='innerText'>We implement a variety of security measures when a user enters, submits, or accesses their information to maintain the safety of your personal information.</div>
            <br />
            <div className='innerText'>All transactions are processed through a gateway provider and are not stored or processed on our servers.</div>
            <span id='coUs'></span>
            <br />
            <div className='grayText'><strong>Do we use 'cookies'?</strong></div>
            <br />
            <div className='innerText'>Yes. Cookies are small files that a site or its service provider transfers to your computer's hard drive through your Web browser (if you allow) that enables the site's or service provider's systems to recognize your browser and capture and remember certain information. For instance, we use cookies to help us remember and process the items in your shopping cart. They are also used to help us understand your preferences based on previous or current site activity, which enables us to provide you with improved services. We also use cookies to help us compile aggregate data about site traffic and site interaction so that we can offer better site experiences and tools in the future.</div>
            <div className='innerText'><br /><strong>We use cookies to:</strong></div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Understand and save user's preferences for future visits.</div>
            <div className='innerText'><br />You can choose to have your computer warn you each time a cookie is being sent, or you can choose to turn off all cookies. You do this through your browser settings. Since browser is a little different, look at your browser's Help Menu to learn the correct way to modify your cookies.<br /></div>
            <br />
            <div className='innerText'>If you turn cookies off, Some of the features that make your site experience more efficient may not function properly.It won't affect the user's experience that make your site experience more efficient and may not function properly.</div>
            <br />
            <span id='trDi'></span>
            <br />
            <div className='grayText'><strong>Third-party disclosure</strong></div>
            <br />
            <div className='innerText'>We do not sell, trade, or otherwise transfer to outside parties your Personally Identifiable Information.</div>
            <span id='trLi'></span>
            <br />
            <div className='grayText'><strong>Third-party links</strong></div>
            <br />
            <div className='innerText'>We do not include or offer third-party products or services on our website.</div>
            <span id='gooAd'></span>
            <br />
            <div className='blueText'><strong>Google</strong></div>
            <br />
            <div className='innerText'>Google's advertising requirements can be summed up by Google's Advertising Principles. They are put in place to provide a positive experience for users. https://support.google.com/adwordspolicy/answer/1316548?hl=en <br /><br /></div>
            <div className='innerText'>We have not enabled Google AdSense on our site but we may do so in the future.</div>
            <span id='calOppa'></span>
            <br />
            <div className='blueText'><strong>California Online Privacy Protection Act</strong></div>
            <br />
            <div className='innerText'>CalOPPA is the first state law in the nation to require commercial websites and online services to post a privacy policy.  The law's reach stretches well beyond California to require any person or company in the United States (and conceivably the world) that operates websites collecting Personally Identifiable Information from California consumers to post a conspicuous privacy policy on its website stating exactly the information being collected and those individuals or companies with whom it is being shared. -  See more at: http://consumercal.org/california-online-privacy-protection-act-caloppa/#sthash.0FdRbT51.dpuf<br /></div>
            <div className='innerText'><br /><strong>According to CalOPPA, we agree to the following:</strong><br /></div>
            <div className='innerText'>Users can visit our site anonymously.</div>
            <div className='innerText'>Once this privacy policy is created, we will add a link to it on our home page or as a minimum, on the first significant page after entering our website.<br /></div>
            <div className='innerText'>Our Privacy Policy link includes the word 'Privacy' and can easily be found on the page specified above.</div>
            <div className='innerText'><br />You will be notified of any Privacy Policy changes:</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> On our Privacy Policy Page<br /></div>
            <div className='innerText'>Can change your personal information:</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> By logging in to your account</div>
            <div className='innerText'><br /><strong>How does our site handle Do Not Track signals?</strong><br /></div>
            <div className='innerText'>We honor Do Not Track signals and Do Not Track, plant cookies, or use advertising when a Do Not Track (DNT) browser mechanism is in place. </div>
            <div className='innerText'><br /><strong>Does our site allow third-party behavioral tracking?</strong><br /></div>
            <div className='innerText'>It's also important to note that we do not allow third-party behavioral tracking</div>
            <span id='coppAct'></span>
            <br />
            <div className='blueText'><strong>COPPA (Children Online Privacy Protection Act)</strong></div>
            <br />
            <div className='innerText'>When it comes to the collection of personal information from children under the age of 13 years old, the Children's Online Privacy Protection Act (COPPA) puts parents in control.  The Federal Trade Commission, United States' consumer protection agency, enforces the COPPA Rule, which spells out what operators of websites and online services must do to protect children's privacy and safety online.<br /><br /></div>
            <div className='innerText'>We do not specifically market to children under the age of 13 years old.</div>
            <div className='innerText'>Do we let third-parties, including ad networks or plug-ins collect PII from children under 13?</div>
            <span id='ftcFip'></span>
            <br />
            <div className='blueText'><strong>Fair Information Practices</strong></div>
            <br />
            <div className='innerText'>The Fair Information Practices Principles form the backbone of privacy law in the United States and the concepts they include have played a significant role in the development of data protection laws around the globe. Understanding the Fair Information Practice Principles and how they should be implemented is critical to comply with the various privacy laws that protect personal information.<br /><br /></div>
            <div className='innerText'><strong>In order to be in line with Fair Information Practices we will take the following responsive action, should a data breach occur:</strong></div>
            <div className='innerText'>We will notify you via email</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Within 7 business days</div>
            <div className='innerText'><br />We also agree to the Individual Redress Principle which requires that individuals have the right to legally pursue enforceable rights against data collectors and processors who fail to adhere to the law. This principle requires not only that individuals have enforceable rights against data users, but also that individuals have recourse to courts or government agencies to investigate and/or prosecute non-compliance by data processors.</div>
            <span id='canSpam'></span>
            <br />
            <div className='blueText'><strong>CAN SPAM Act</strong></div>
            <br />
            <div className='innerText'>The CAN-SPAM Act is a law that sets the rules for commercial email, establishes requirements for commercial messages, gives recipients the right to have emails stopped from being sent to them, and spells out tough penalties for violations.<br /><br /></div>
            <div className='innerText'><strong>We collect your email address in order to:</strong></div>
            <div className='innerText'><br /><strong>To be in accordance with CANSPAM, we agree to the following:</strong></div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Not use false or misleading subjects or email addresses.</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Identify the message as an advertisement in some reasonable way.</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Include the physical address of our business or site headquarters.</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Monitor third-party email marketing services for compliance, if one is used.</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Honor opt-out/unsubscribe requests quickly.</div>
            <div className='innerText'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <strong>&bull;</strong> Allow users to unsubscribe by using the link at the bottom of each email.</div>
            <div className='innerText'><strong><br />If at any time you would like to unsubscribe from receiving future emails, you can email us at matthew.slotkin@gmail.com</strong></div>
             and we will promptly remove you from <strong>ALL</strong> correspondence.
        </div>
        <br />
        <span id='ourCon'></span>
        <br />
        <div className='blueText'><strong>Contacting Us</strong></div>
        <br />
        <div className='innerText'>If there are any questions regarding this privacy policy, you may contact us using the information below.<br /><br /></div>
        <div className='innerText'>Treople</div>
        <div className='innerText'>290 Stanyan St</div>
        San Francisco, California 94118
        <div className='innerText'>United States</div>
        <div className='innerText'>matthew.slotkin@gmail.com</div>
        <div className='innerText'>646-334-8539</div>
        <div className='innerText'><br />Last Edited on 2018-01-12</div>
    </Grid>

  }
};
