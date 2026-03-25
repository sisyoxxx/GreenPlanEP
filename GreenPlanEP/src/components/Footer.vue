<template>
    <footer class="site-footer">
      <div class="footer-inner">
        <div class="footer-col">
          <h4>关于我们</h4>
          <p>专注优质绿植种子，让生命发芽。</p>
        </div>
        <div class="footer-col">
          <h4>帮助中心</h4>
          <ul>
            <li><a href="#">购买指南</a> <a href="#">售后政策</a></li>

          </ul>
        </div>
      </div>
      <div class="copyright">
        &copy; 2026 绿植种子平台 版权所有
      </div>
    </footer>
  </template>
  
  <style scoped>
  .site-footer {
    background: #2d3436;
    color: #bdc3c7;
    padding: 30px;
    margin-top: auto;
  }
  
  .footer-inner {
    max-height: 50px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 40px;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 50px;
    margin-bottom: 0px;
  }
  
  .footer-col h4 {
    color: #fff;
    margin-bottom: 15px;
  }
  
  .footer-col ul {
    list-style: none;
  }
  
  .footer-col li {
    margin-bottom: 8px;
  }
  
  .footer-col a {
    color: #bdc3c7;
    text-decoration: none;
  }
  
  .footer-col a:hover {
    color: #fff;
  }
  
  .copyright {
    text-align: center;
    border-top: 1px solid #444;
    padding-top: 20px;
    font-size: 0.85rem;
  }
  </style>